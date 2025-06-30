package study.goorm.domain.history.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.goorm.domain.history.domain.entity.Hashtag;
import study.goorm.domain.history.domain.entity.HistoryImage;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class HistoryResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyHistoryResult {

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
        private String img_url;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyHistoryResult {
        private Long memberId;
        private String memberImageUrl;
        private String nickName;
        private String clokeyId;
        private String contents;
        private List<String> images;
        private List<String> hashtags;
        private int likeCount;
        private boolean liked;
        private LocalDate date;
        private List<DailyHistoryItemResult> clothes;
        private Long historyId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyHistoryItemResult {
        private Long clothId;
        private String clothName;
        private String clothImageUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikedUsersResult {
        private List<LikedUsersResultItem> likedUsers;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikedUsersResultItem {
        private Long memberId;
        private String clokeyId;
        private String nickName;
        private String imageUrl;
        private boolean me;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryCreateResult {
        private Long id;
    }

}
