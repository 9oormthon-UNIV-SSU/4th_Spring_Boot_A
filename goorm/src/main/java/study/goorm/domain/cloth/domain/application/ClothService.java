package study.goorm.domain.cloth.domain.application;

import study.goorm.domain.cloth.domain.dto.ClothResponseDTO;
import study.goorm.domain.model.enums.ClothSort;

public interface ClothService {
    // 옷 조회(수정용)
    ClothResponseDTO.ClothEditViewResult getClothEditView(Long clothId);

    // 유저 옷장 조회
    ClothResponseDTO.MemberClosetResult getMemberCloset(String clokeyId, ClothSort sort, int page, int size);

}
