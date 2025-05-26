package study.goorm.domain.history.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.goorm.domain.cloth.application.ClothImageQueryService;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.cloth.domain.entity.ClothImage;
import study.goorm.domain.cloth.domain.repository.ClothImageRepository;
import study.goorm.domain.history.converter.HistoryConverter;
import study.goorm.domain.history.domain.entity.*;
import study.goorm.domain.history.domain.repository.*;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.history.exception.HistoryException;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.member.domain.exception.MemberException;
import study.goorm.domain.member.domain.repository.MemberRepository;
import study.goorm.global.error.code.status.ErrorStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService{
    private final MemberRepository memberRepository;
    private final HistoryRepository historyRepository;
    private final CommentRepository commentRepository;
    private final HashtagHistoryRepository hashtagHistoryRepository;
    private final HistoryClothRepository historyClothRepository;
    private final HistoryImageRepository historyImageRepository;
    private final MemberLikeRepository memberLikeRepository;
    private final HistoryImageQueryService historyImageQueryService;
    private final ClothImageRepository clothImageRepository;
    private final ClothImageQueryService clothImageQueryService;


    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.MonthlyHistoryResult getMonthlyHistory(String clokeyId, String month) {
        Member member;
        if (clokeyId == null) { // clokeyId가 null이면 "1"로 가정하고 구현
            clokeyId = "clo001";
        }
        member = memberRepository.findByClokeyId(clokeyId)
                .orElseThrow(()-> new MemberException(ErrorStatus.NO_SUCH_MEMBER));

        List<History> histories = historyRepository.findAllByMemberIdAndMonth(member.getId(), month);
        if (histories.isEmpty()) {
            throw new HistoryException(ErrorStatus.BAD_DATE_TYPE);
        }

        Map<Long, String> firstImagesOfHistory = historyImageQueryService.getFirstImageUrlMap(histories);

        return HistoryConverter.toMonthlyHistoryResult(member, firstImagesOfHistory, histories);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.DailyHistoryResult getDailyHistory(Long historyId) {
        // history 불러옴
        History history = historyRepository.findById(historyId)
                .orElseThrow(()-> new HistoryException(ErrorStatus.NO_SUCH_HISTORY));

        Member member = history.getMember();

        // 기록 사진 리스트
        List<HistoryImage> historyImages = historyImageRepository.findAllByHistory(history);
        if (historyImages.isEmpty()) {
            throw new HistoryException(ErrorStatus.NO_HISTORY_IMAGE);
        }
        //historyImages에서 ImageUrl만 추출
        List<String> historyImageUrls = historyImages.stream()
                .map(HistoryImage::getImageUrl)
                .collect(Collectors.toList());

        List<HashtagHistory> hashtagHistories = hashtagHistoryRepository.findByHistory(history);

        //  각 HashtagHistory에서 Hashtag만 추출
        List<Hashtag> hashtags = hashtagHistories.stream()
                .map(HashtagHistory::getHashtag)
                .collect(Collectors.toList());

        // memberId = 1로 구현
        boolean liked = memberLikeRepository.existsByHistoryIdAndMemberId(historyId, 1L);

        List<HistoryCloth> historyClothes = historyClothRepository.findAllByHistory(history);
        // 각 historyClothes에서 clothes만 추출
        List<Cloth> clothes = historyClothes.stream()
                .map(HistoryCloth::getCloth)
                .collect(Collectors.toList());

        // 각 옷의 첫번째 사진만 가져옴
        Map<Long, String> firstImagesOfCloth = clothImageQueryService.getFirstImageUrlMap(clothes);

        return HistoryConverter.toDailyHistoryResult(history, member, liked, historyImageUrls, hashtags, clothes, firstImagesOfCloth);
    }

    @Override
    @Transactional
    public void deleteHistory(Long historyId) {
        History history = historyRepository.findById(historyId)
                .orElseThrow(()-> new HistoryException(ErrorStatus.NO_SUCH_HISTORY));

        // 매핑 테이블 삭제
        commentRepository.deleteAllByHistory(history);
        hashtagHistoryRepository.deleteAllByHistory(history);
        historyClothRepository.deleteAllByHistory(history);
        historyImageRepository.deleteAllByHistory(history);
        memberLikeRepository.deleteAllByHistory(history);

        // 최종 기록 삭제
        historyRepository.delete(history);
    }
}