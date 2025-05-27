package study.goorm.domain.history.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.cloth.domain.repository.ClothRepository;
import study.goorm.domain.history.converter.HistoryConverter;
import study.goorm.domain.history.domain.entity.*;
import study.goorm.domain.history.domain.repository.*;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.history.exception.HistoryException;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.member.domain.repository.MemberRepository;
import study.goorm.global.error.code.status.ErrorStatus;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final MemberRepository memberRepository;
    private final HistoryRepository historyRepository;
    private final HistoryImageRepository historyImageRepository;
    private final HashtagHistoryRepository hashtagHistoryRepository;
    private final CommentRepository commentRepository;
    private final ClothRepository clothRepository;

    // 월별 기록 조회
    @Transactional(readOnly = true)
    @Override
    public HistoryResponseDTO.MonthlyHistoryPreview getMonthlyPreview(String clokeyId, String date) {

        // 날짜 형식 검증
        try {
            YearMonth.parse(date); // YYYY-MM 형식 검증만
        } catch (DateTimeParseException e) {
            throw new HistoryException(ErrorStatus.INVALID_DATE_FORMAT);
        }

        // clokeyId가 null이면 자신의 기록 열람
        Member member;
        if (clokeyId == null) {
            member = memberRepository.findById(1L)
                    .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_MEMBER));
        } else {
            member = memberRepository.findByClokeyId(clokeyId)
                    .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_MEMBER));
        }

        // 기록 조회
        List<History> histories = historyRepository.findHistoriesByMemberIdAndYearMonth(member.getId(), date);
        if (histories.isEmpty()) {
            throw new HistoryException(ErrorStatus.NO_SUCH_HISTORY);
        }


        List<Long> historyIds = histories.stream()
                .map(History::getId)
                .collect(Collectors.toList());

        // 사진 조회
        List<HistoryImage> historyImages = historyImageRepository.findAllByHistoryIdIn(historyIds);

        // historyId → 첫 번째 이미지 URL
        Map<Long, String> firstImagesOfHistory = historyImages.stream()
                .collect(Collectors.groupingBy(
                        img -> img.getHistory().getId(),
                        Collectors.mapping(HistoryImage::getImageUrl, Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.isEmpty() ? "비공개입니다" : list.get(0)
                        ))
                ));

        List<HistoryResponseDTO.MonthlyHistoryItemResult> resultList = histories.stream()
                .map(history -> HistoryResponseDTO.MonthlyHistoryItemResult.builder()
                        .historyId(history.getId())
                        .date(history.getHistoryDate().toString())
                        .imageUrl(firstImagesOfHistory.getOrDefault(history.getId(), "비공개입니다"))
                        .build())
                .collect(Collectors.toList());

        return HistoryConverter.toMonthlyHistoryPreview(member, resultList);
    }

    // 일별 기록 조회
    @Transactional(readOnly = true)
    @Override
    public HistoryResponseDTO.DailyHistoryPreview getDailyPreview(Long historyId) {

        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_HISTORY));

        Member member = history.getMember();

        // 이미지 조회
        List<HistoryImage> historyImages = historyImageRepository.findByHistoryId(historyId);
        List<String> images = historyImages.stream()
                .map(HistoryImage::getImageUrl)
                .toList();

        // 해시태그 조회
        List<HashtagHistory> hashtagHistories = hashtagHistoryRepository.findByHistoryId(historyId);

        // 기록의 해시태그들 조회
        List<Hashtag> hashtags = hashtagHistories.stream()
                .map(HashtagHistory::getHashtag)
                .toList(); // .collect(Collectors.toList()이랑 동일

        List<String> hashtagNameList = hashtags.stream()
                .map(Hashtag::getName)
                .toList();

        // 댓글 갯수 세기
        long commentCount = commentRepository.countByHistoryId(historyId);

        List<Cloth> cloths =  clothRepository.findByMemberId(member.getId());

        List<HistoryResponseDTO.DailyHistoryClothesPreview> clothPreviews = cloths.stream()
                .map(cloth -> HistoryResponseDTO.DailyHistoryClothesPreview.builder()
                        .clothId(cloth.getId())
                        .clothImageUrl(cloth.getClothUrl())  // 이미지 필드 맞게 수정
                        .clothName(cloth.getName())
                        .build())
                .toList();


        return HistoryConverter.toDailyHistoryPreview(member, history,images, hashtagNameList, commentCount,clothPreviews);
    }

}
