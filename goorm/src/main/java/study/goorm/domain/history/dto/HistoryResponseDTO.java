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
    public static class HistoryGetDaily {
        private Long memberId;
        private String memberImageUrl;
        private String nickName;
        private String clokeyId;
        private String content;
        private List<String> images;
        private List<String> hashtags;
        private int likeCount;
        private boolean liked;
        private LocalDate date;
        private List<HistoryGetCloth> clothes;
        private long clothId;
        private long historyId;
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryGetCloth {
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
}
