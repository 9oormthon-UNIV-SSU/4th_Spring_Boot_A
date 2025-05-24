package study.goorm.domain.history.converter;

import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.member.domain.entity.Member;

import java.util.List;
import java.util.Map;

public class HistoryConverter {
    public static HistoryResponseDTO.MontlyHistoryResult toMonthlyHistoryResult(Member member, Map<Long, String> firstImagesOfHistory, List<History> histories){
        return null;
    }
}
