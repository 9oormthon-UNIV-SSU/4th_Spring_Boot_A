package study.goorm.domain.history.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.goorm.domain.model.enums.Visibility;

import java.util.List;

public class HistoryRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryCreateRequest {

        @Size(max = 200, message = "내용은 최대 200자까지 입력 가능합니다.")
        private String content;

        private List<Long> clothes;

        private List<String> hashtags;

        @NotNull(message = "날짜는 필수입니다.")
        private String date;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryUpdateRequest {

        @Size(max = 200, message = "내용은 최대 200자까지 입력 가능합니다.")
        private String content;

        private List<Long> clothes;

        private List<String> hashtags;

        private Visibility visibility;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentUpdateRequest {
        @Size(max = 50, message = "댓글은 최대 50자까지 입력 가능합니다.")
        @NotNull
        @NotBlank
        private String content;
    }
}
