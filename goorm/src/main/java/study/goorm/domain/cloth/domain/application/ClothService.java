package study.goorm.domain.cloth.domain.application;

import study.goorm.domain.cloth.domain.dto.ClothResponseDTO;

public interface ClothService {
    ClothResponseDTO.ClothEditViewResult getClothEditView(Long clothId);
}
