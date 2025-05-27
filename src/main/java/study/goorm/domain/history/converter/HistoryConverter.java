package study.goorm.domain.history.converter;

import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.member.domain.entity.Member;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HistoryConverter {

    public static HistoryResponseDTO.MonthlyHistoryPreview toMonthlyHistoryPreview(Member member, List<HistoryResponseDTO.MonthlyHistoryItemResult> result) {

        return HistoryResponseDTO.MonthlyHistoryPreview.builder()
                .memberId(member.getId())
                .nickName(member.getNickname())
                .histories(result)
                .build();
    }



}
