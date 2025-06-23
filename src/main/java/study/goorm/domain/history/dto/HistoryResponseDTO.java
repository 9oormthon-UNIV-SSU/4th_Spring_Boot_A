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
    public static class MonthlyHistoryPreview {
        private Long memberId;
        private String nickName;
        private List<MonthlyHistoryItemResult> histories;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyHistoryItemResult {
        private Long historyId;
        private String date;
        private String imageUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyHistoryPreview {

        private Long memberId;
        private Long historyId;
        private String memberImageUrl;
        private String nickName;
        private String clokeyId;
        private String contents;
        private List<String> imageUrl;
        private List<String> hashtags;
        private long likeCount;
        private long commentCount;
        private LocalDate date;
        private List<DailyHistoryClothesPreview> cloths;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyHistoryClothesPreview {
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
