package study.goorm.domain.history.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

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
}
