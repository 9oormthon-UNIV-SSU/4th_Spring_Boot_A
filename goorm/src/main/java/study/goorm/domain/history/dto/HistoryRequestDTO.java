package study.goorm.domain.history.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.goorm.domain.cloth.exception.annotation.CheckLowerUpperTempBound;
import study.goorm.domain.model.enums.Season;
import study.goorm.domain.model.enums.ThicknessLevel;

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

//        private Visibility visibility;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryLike {

        private Long historyId;

        private boolean liked;

    }
}
