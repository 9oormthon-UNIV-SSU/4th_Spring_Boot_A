package study.goorm.domain.history.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Column;
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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryLikeResult {

        private Long historyId;

        private boolean liked;

        private int likeCount;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryLikedUserResultList {
        List<HistoryLikedUserResult> likedUsers;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryLikedUserResult {
        private Long memberId;
        private String clokeyId;
        private String nickname;
        private boolean followStatus;
        private String imageUrl;
        private boolean isMe;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryCommentWriteResult {
        Long commentId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryCommentResult {
        List<CommentResult> comments;
        int totalPage;
        int totalElements;

        @JsonProperty("isFirst") // JSON 직렬화 시 "isFirst" 사용
        private boolean isFirst;

        @JsonIgnore // "first" 필드 직렬화 방지
        public boolean isFirst() {
            return isFirst;
        }

        @JsonProperty("isLast") // JSON 직렬화 시 "isLast" 사용
        private boolean isLast;

        @JsonIgnore // "last" 필드 직렬화 방지
        public boolean isLast() {
            return isLast;
        }
    }


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonPropertyOrder({"commentId", "clokeyId", "nickName", "userImageUrl", "content", "replyResults"})
    public static class CommentResult {
        Long commentId;
        String clokeyId;
        String nickName;
        String userImageUrl;
        String content;
        List<ReplyResult> replyResults;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReplyResult {
        Long commentId;
        String clokeyId;
        String nickName;
        String userImageUrl;
        String content;
    }
}
