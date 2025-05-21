package study.goorm.domain.cloth.application;

import study.goorm.domain.cloth.dto.ClothResponseDTO;
import study.goorm.domain.model.enums.ClothSort;

public interface ClothService {

    ClothResponseDTO.ClothEditViewResult getClothEditView(Long clothId);

    ClothResponseDTO.MemberClosetResult getMemberCloset(String clokeyId, ClothSort sort, int page, int size);
}
