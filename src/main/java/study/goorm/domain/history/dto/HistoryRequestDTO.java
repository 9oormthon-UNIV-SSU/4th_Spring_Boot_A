package study.goorm.domain.history.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.goorm.domain.model.enums.Visibility;

import java.time.LocalDate;
import java.util.List;

public class HistoryRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryCreateRequest {

        @Column(length = 200)
        private String content;

        private List<Long> clothes;

        private List<String> hashtags;

        private LocalDate date;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryUpdateRequest {

        @Column(length = 200)
        private String content;

        private List<Long> clothes;

        private List<String> hashtags;

        private Visibility visibility;
    }

    // 좋아요
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryLikeRequest {

        private Long historyId;

        private boolean liked;
    }

    // 댓글 작성
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentWriteRequest {

        private Long commentId;

        private String content;
    }
}
