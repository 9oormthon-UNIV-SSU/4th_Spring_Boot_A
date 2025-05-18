package study.goorm.domain.cloth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.goorm.domain.cloth.converter.ClothConverter;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.cloth.domain.entity.ClothImage;
import study.goorm.domain.cloth.domain.repository.ClothImageRepository;
import study.goorm.domain.cloth.domain.repository.ClothRepository;
import study.goorm.domain.cloth.dto.ClothResponseDTO;
import study.goorm.domain.cloth.exception.ClothException;
import study.goorm.global.error.code.status.ErrorStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClothServiceImpl implements ClothService {

    private final ClothRepository clothRepository;
    private final ClothImageRepository clothImageRepository;

    @Override
    @Transactional(readOnly = true)
    public ClothResponseDTO.ClothEditViewResult getClothEditView(Long clothId) {

        Cloth cloth = clothRepository.findById(clothId)
                .orElseThrow(()-> new ClothException(ErrorStatus.NO_SUCH_CLOTH));

        List<ClothImage> clothImageUrls = clothImageRepository.findAllByCloth(cloth);

        String firstImageUrl = clothImageUrls.stream()
                .findFirst()
                .map(ClothImage::getImageUrl)
                .orElseThrow(() -> new ClothException(ErrorStatus.NO_ClOTH_IMAGE));


        return ClothConverter.toClothEditViewResult(cloth, firstImageUrl);
    }
}
