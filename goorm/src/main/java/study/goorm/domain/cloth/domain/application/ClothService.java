package study.goorm.domain.cloth.domain.application;

import org.springframework.web.multipart.MultipartFile;
import study.goorm.domain.cloth.domain.dto.ClothRequestDTO;
import study.goorm.domain.cloth.domain.dto.ClothResponseDTO;
import study.goorm.domain.model.enums.ClothSort;

public interface ClothService {
    // 옷 조회(수정용)
    ClothResponseDTO.ClothEditViewResult getClothEditView(Long clothId);

    // 유저 옷장 조회
    ClothResponseDTO.MemberClosetResult getMemberCloset(String clokeyId, ClothSort sort, int page, int size);

    // 옷 추가
    ClothResponseDTO.ClothCreateResult createCloth(ClothRequestDTO.ClothCreateRequest clothCreateResult, MultipartFile image);

    // 옷 삭제
    void deleteCloth(Long clothId);
}
