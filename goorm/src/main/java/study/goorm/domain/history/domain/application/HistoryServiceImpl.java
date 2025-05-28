package study.goorm.domain.history.domain.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.goorm.domain.history.domain.converter.HistoryConverter;
import study.goorm.domain.history.domain.dto.HistoryResponseDTO;
import study.goorm.domain.history.domain.entity.HashtagHistory;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryImage;
import study.goorm.domain.history.domain.exception.HistoryException;
import study.goorm.domain.history.domain.repository.*;
import study.goorm.domain.member.domain.application.MemberService;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.member.domain.exception.MemberException;
import study.goorm.global.error.code.status.ErrorStatus;
import study.goorm.domain.history.domain.entity.Hashtag;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;
    private final HistoryImageRepository historyImageRepository;
    private final HistoryConverter historyConverter;
    private final MemberService memberService;


    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.MonthlyHistoriesResult getMonthlyHistories(String clokeyId, YearMonth month){

        Member member = (clokeyId == null)
                ? memberService.getCurrentMember()
                : memberService.findByClokeyId(clokeyId)
                .orElseThrow(() -> new MemberException(ErrorStatus.NO_SUCH_MEMBER));

        List<History> histories = historyRepository.findByMemberIdAndMonth(member.getId(), month);

        // 이미지 로직 따로 빼는게 더 나을까요? 아니면 그냥 두는게 나을까요
        List<HistoryResponseDTO.MonthlyHistoriesResult.HistoryDto> historiesDtos = histories.stream()
                .map(history -> {
                    String imageUrl = historyImageRepository
                            .findFirstByHistoryIdOrderByCreatedAtAsc(history.getId())
                            .map(HistoryImage::getImageUrl)
                            .orElseThrow(() -> new HistoryException(ErrorStatus.NO_CLOTH_IMAGE));
                    return historyConverter.toMonthlyHistoryDto(history, imageUrl);
                })
                .collect(Collectors.toList());

        return historyConverter.toMonthlyHistoriesResult(member, historiesDtos);
    }

    private final HashtagHistoryRepository hashtagHistoryRepository;
    private final HistoryClothRepository historyClothRepository;
    private final MemberLikeRepository memberLikeRepository;


    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.DailyHistoryResult getDailyHistory(Long historyId){

        Member currentMember = memberService.getCurrentMember();

        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_HISTORY));

        if(!history.getMember().getId().equals(currentMember.getId())){
            throw new HistoryException(ErrorStatus.NO_GRANT_HISTORY);
        }

        List<String> imageUrls = historyImageRepository.findAllByHistory(history)
                .stream()
                .map(HistoryImage::getImageUrl)
                .toList();

        List<String> hashtags = hashtagHistoryRepository.findAllByHistory(history)
                .stream()
                .map(hashtagHistory -> hashtagHistory.getHashtag().getName())
                .toList();

        boolean liked = memberLikeRepository.existsByHistoryAndMember(history, currentMember);

        List<HistoryResponseDTO.DailyHistoryResult.ClothDto> clothDtos = historyClothRepository.findAllByHistory(history)
                .stream()
                .map(mapping -> historyConverter.toClothDto(mapping.getCloth()))
                .toList();


        return historyConverter.toDailyHistoryResult(history, imageUrls, hashtags, liked, clothDtos);
    }
}
