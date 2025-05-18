package study.goorm.domain.cloth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.goorm.domain.model.enums.Season;
import study.goorm.domain.model.enums.ThicknessLevel;

import java.util.List;

public class ClothResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClothEditViewResult {
        private Long id;
        private String name;
        private List<Season> seasons;
        private int tempUpperBound;
        private int tempLowerBound;
        private ThicknessLevel thicknessLevel;
        private String clothUrl;
        private String brand;
        private String imageUrl;
        private Long categoryId;
    }
}
