package study.goorm.domain.history.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.goorm.domain.history.domain.entity.HistoryImage;

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
        private List<HistoryGetMonthlyResult> histories;
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryGetMonthlyResult {
        private Long historyId;
        private LocalDate date;
        private String imageUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryGetDaily {
        private Long memberId;
        private String memberImageUrl;
        private String nickName;
        private String clokeyId;
        private String content;
        private List<String> imageUrl;
        private List<String> hashtags;
        private int likeCount;
        private int commentCount;
        private boolean liked;
        private LocalDate date;
        private List<HistoryGetDailyCloth> cloths;
        private long clothId;
        private long historyId;
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryGetDailyCloth {
        private Long clothId;
        private String clothImageUrl;
        private String clothName;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryCreateResult {
        private Long historyId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryUpdateResult {
        private Long historyId;
    }
}
