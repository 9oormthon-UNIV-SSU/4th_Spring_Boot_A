package study.goorm.domain.cloth.application;

import org.springframework.web.multipart.MultipartFile;
import study.goorm.domain.cloth.dto.ClothRequestDTO;
import study.goorm.domain.cloth.dto.ClothResponseDTO;
import study.goorm.domain.model.enums.ClothSort;

public interface ClothService {

    ClothResponseDTO.ClothEditViewResult getClothEditView(Long clothId);

    ClothResponseDTO.MemberClosetResult getMemberCloset(String clokeyId, ClothSort sort, int page, int size);

    ClothResponseDTO.ClothCreateResult createCloth(ClothRequestDTO.ClothCreateRequest clothCreateResult, MultipartFile image);

    void deleteCloth(Long clothId);
}
