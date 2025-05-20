package study.goorm.domain.cloth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.goorm.domain.model.enums.Season;
import study.goorm.domain.model.enums.ThicknessLevel;

import java.util.List;

public class ClothRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClothCreateRequest {

        private Long memberId;

        private Long categoryId;

        private String name;

        private List<Season> seasons;

        private Integer tempUpperBound;

        private Integer tempLowerBound;

        private ThicknessLevel thicknessLevel;

        private String clothUrl;

        private String brand;
    }
}
