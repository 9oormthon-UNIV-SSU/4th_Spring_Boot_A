package study.goorm.domain.history.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class HistoryResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyHistoriesResult{
        private Long id;
        private String nickName;
        private List<HistoryDto> histories;

        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class HistoryDto {
            private Long historyId;
            private LocalDate date;
            private String imageUrl;
        }
    }
}
