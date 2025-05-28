package study.goorm.domain.history.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

        @NotNull(message = "날짜는 필수입니다.")
        private String Date;
    }
}
