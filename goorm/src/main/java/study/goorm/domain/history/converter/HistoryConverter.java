package study.goorm.domain.history.converter;

import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.member.domain.entity.Member;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HistoryConverter {
    public static HistoryResponseDTO.MonthlyHistoryResult toMonthlyHistoryResult(Member member, Map<Long, String> firstImagesOfHistory, List<History> histories){
        return HistoryResponseDTO.MonthlyHistoryResult.builder()
                .memberId(member.getId())
                .nickName(member.getNickname())
                .histories(toMonthlyHistoryItemResult(firstImagesOfHistory, histories))
                .build();
    }

    public static List<HistoryResponseDTO.MonthlyHistoryItemResult> toMonthlyHistoryItemResult(Map<Long, String> firstImagesOfHistory, List<History> histories) {
        return histories.stream()
                .map(history ->  HistoryResponseDTO.MonthlyHistoryItemResult.builder()
                        .historyId(history.getId())
                        .date(history.getHistoryDate().toString())
                        .img_url(firstImagesOfHistory.get(history.getId()))
                        .build()
                )
                .collect(Collectors.toList());
    }
}
