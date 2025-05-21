package study.goorm.domain.cloth.domain.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import study.goorm.domain.cloth.domain.converter.ClothConverter;
import study.goorm.domain.cloth.domain.dto.ClothResponseDTO;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.cloth.domain.entity.ClothImage;
import study.goorm.domain.cloth.domain.exception.ClothException;
import study.goorm.domain.cloth.domain.repository.ClothImageRepository;
import study.goorm.domain.cloth.domain.repository.ClothRepository;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.member.domain.repository.MemberRepository;
import study.goorm.domain.member.domain.exception.MemberException;
import study.goorm.domain.cloth.domain.application.ClothImageQueryService;

import study.goorm.domain.model.enums.ClothSort;
import study.goorm.global.error.code.status.ErrorStatus;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClothServiceImpl implements ClothService {

    private final ClothRepository clothRepository;
    private final ClothImageRepository clothImageRepository;

    // 옷 조회(수정용)
    @Override
    @Transactional(readOnly = true)
    public ClothResponseDTO.ClothEditViewResult getClothEditView(Long clothId) {

        Cloth cloth = clothRepository.findById(clothId)
                .orElseThrow(() -> new ClothException(ErrorStatus.NO_SUCH_CLOTH));

        List<ClothImage> clothImageUrls = clothImageRepository.findAllByCloth(cloth);

        String firstImageUrl = clothImageUrls.stream()
                .findFirst()
                .map(ClothImage::getImageUrl)
                .orElseThrow(() -> new ClothException(ErrorStatus.NO_ClOTH_IMAGE));

        return ClothConverter.toClothEditViewResult(cloth, firstImageUrl);
    }

    private final MemberRepository memberRepository;
    private final ClothImageQueryService clothImageQueryService;

    // 유저 옷장 조회
    @Override
    @Transactional(readOnly = true)
    public ClothResponseDTO.MemberClosetResult getMemberCloset(String clokeyId, ClothSort sort, int page, int size) {

        Member member = memberRepository.findByClokeyId(clokeyId)
                .orElseThrow(()-> new MemberException(ErrorStatus.NO_SUCH_MEMBER));
        PageRequest pageRequest = PageRequest.of(page,size);

        Page<Cloth> clothes;

        if (sort.equals(ClothSort.LATEST)){
            clothes = clothRepository.findByMemberOrderByCreatedAtDesc(member, pageRequest);
        }else if(sort.equals(ClothSort.OLDEST)){
            clothes = clothRepository.findByMemberOrderByCreatedAtAsc(member,pageRequest);
        }else if(sort.equals(ClothSort.WEAR)){
            clothes = clothRepository.findByMemberOrderByWearNumDesc(member,pageRequest);
        }else {
            clothes = clothRepository.findByMemberOrderByWearNumAsc(member,pageRequest);
        }

        Map<Long, String> firstImagesOfCloth = clothImageQueryService.getFirstImageUrlMap(clothes);

        return ClothConverter.toMemberClosetResult(member,firstImagesOfCloth,clothes);
    }
}
