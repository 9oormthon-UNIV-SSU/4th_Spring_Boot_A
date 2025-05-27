package study.goorm.domain.history.domain.converter;

import study.goorm.domain.history.domain.dto.HistoryRequestDTO;
import study.goorm.domain.history.domain.dto.HistoryResponseDTO;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryImage;
import study.goorm.domain.member.domain.entity.Member;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


public class HistoryConverter {

//    public static HistoryResponseDTO.MonthlyHistoriesResult toMonthlyHistories(Member member, List<History> histories) {
//
//        List<HistoryResponseDTO.MonthlyHistoriesResult.HistoryDto> historyDtos = histories.stream()
//                .map(history -> HistoryResponseDTO.MonthlyHistoriesResult.HistoryDto.builder()
//                        .historyId(history.getId())
//                        .date(history.getLocalDate())
//                        .imageUrl(history.getImageUrl())
//                        .build())
//                .collect(Collectors.toList());
//
//        return ;
//
//    }
public static HistoryResponseDTO.MonthlyHistoriesResult toHistoryGetMonthly(Member member, LocalDate month, List<History> histories, List<HistoryImage> historyImages){
    return HistoryResponseDTO.MonthlyHistoriesResult.builder()
            .memberId(member.getId())
            .nickName(member.getNickname())
            .histories(toHistoryGetList(histories, historyImages))
            .build();
}

}
