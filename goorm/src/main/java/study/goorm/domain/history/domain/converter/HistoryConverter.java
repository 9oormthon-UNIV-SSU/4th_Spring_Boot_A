package study.goorm.domain.history.domain.converter;

import org.springframework.stereotype.Component;
import study.goorm.domain.history.domain.dto.HistoryResponseDTO;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.member.domain.entity.Member;

import java.util.List;


@Component
public class HistoryConverter {
    public HistoryResponseDTO.MonthlyHistoriesResult.HistoryDto toMonthlyHistoryDto(History history, String imageUrl){
        return HistoryResponseDTO.MonthlyHistoriesResult.HistoryDto.builder()
                .historyId(history.getId())
                .date(history.getHistoryDate())
                .imageUrl(imageUrl)
                .build();
    }

    public HistoryResponseDTO.MonthlyHistoriesResult toMonthlyHistoriesResult(
            Member member, List<HistoryResponseDTO.MonthlyHistoriesResult.HistoryDto> historyDtos
    ){
        return HistoryResponseDTO.MonthlyHistoriesResult.builder()
                .id(member.getId())
                .nickName(member.getNickname())
                .histories(historyDtos)
                .build();
    }

}
