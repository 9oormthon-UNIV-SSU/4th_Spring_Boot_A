package study.goorm.domain.history.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.goorm.domain.history.converter.HistoryConverter;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryImage;
import study.goorm.domain.history.domain.repository.HistoryImageRepository;
import study.goorm.domain.history.domain.repository.HistoryRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final MemberRepository memberRepository;
    private final HistoryRepository historyRepository;
    private final HistoryImageRepository historyImageRepository;

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



}
