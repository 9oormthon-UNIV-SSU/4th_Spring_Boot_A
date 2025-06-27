package study.goorm.domain.history.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import study.goorm.domain.model.enums.Visibility;

import java.time.LocalDate;
import java.util.List;

public class HistoryRequestDTO {
    // 기록 추가
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryCreateRequest{
        // 에러 메세지들 설정해줘야함
        @NotNull
        @Size(max = 200)
        private String content;

        @NotNull
        private List<Long> clothes;

        @NotNull
        private List<String> hashtags;

        @NotNull
        @DateTimeFormat(pattern = "YYYY-MM-DD")
        private LocalDate date;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HistoryUpdateRequest {
        @NotBlank(message = "내용은 필수입니다.")
        @Size(max=200, message = "content는 200자 이하입니다.")
        private String content;

        @Size(max = 10, message = "최대 10개의 옷만 허용됩니다.")
        private List<Long> clothes;

        @Size(max = 10, message = "최대 10개의 해시태그만 허용됩니다.")
        private List<String> hashtags;

        @NotNull(message = "visibility는 필수입니다.")
        private Visibility visibility;

    }

  