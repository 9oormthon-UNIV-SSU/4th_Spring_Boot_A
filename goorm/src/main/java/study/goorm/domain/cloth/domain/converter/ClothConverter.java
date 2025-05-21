package study.goorm.domain.cloth.domain.converter;

import study.goorm.domain.cloth.domain.dto.ClothResponseDTO;
import study.goorm.domain.cloth.domain.entity.Cloth;

public class ClothConverter {
    public static ClothResponseDTO.ClothEditViewResult toClothEditViewResult(Cloth cloth, String clothImageUrl){
        return ClothResponseDTO.ClothEditViewResult.builder()
                .id(cloth.getId())
                .brand(cloth.getBrand())
                .categoryId(cloth.getCategory().getId())
                .clothUrl(cloth.getClothUrl())
                .imageUrl(clothImageUrl) // 첫번째사진을 대표사진으로 생각하고 반환하는 것
                .name(cloth.getName())
                .seasons(cloth.getSeason())
                .tempLowerBound(cloth.getTempLowerBound())
                .tempUpperBound(cloth.getTempUpperBound())
                .thicknessLevel(cloth.getThicknessLevel())
                .build();
    }
}
