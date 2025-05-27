package study.goorm.domain.history.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.goorm.domain.cloth.exception.annotation.CheckLowerUpperTempBound;
import study.goorm.domain.model.enums.Season;
import study.goorm.domain.model.enums.ThicknessLevel;

import java.util.List;

public class HistoryRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryCreateRequest {

        private String content;

        private List<Long> clothes;

        private List<String> hashtags;

        private String date;
    }
}
