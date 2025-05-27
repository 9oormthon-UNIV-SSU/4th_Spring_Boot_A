package study.goorm.domain.history.dto;

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
    public static class HistoryGetMonthly {
        private Long memberId;
        private String nickName;
        private List<HistoryGet> histories;
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryGet {
        private Long historyId;
        private LocalDate date;
        private String imageUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryCreateResult {
        private Long historyId;
    }
}
