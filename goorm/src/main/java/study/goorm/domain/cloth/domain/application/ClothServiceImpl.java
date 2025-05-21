package study.goorm.domain.cloth.domain.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import study.goorm.domain.cloth.domain.converter.ClothConverter;
import study.goorm.domain.cloth.domain.dto.ClothRequestDTO;
import study.goorm.domain.cloth.domain.dto.ClothResponseDTO;
import study.goorm.domain.cloth.domain.entity.Category;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.cloth.domain.entity.ClothImage;
import study.goorm.domain.cloth.domain.exception.ClothException;
import study.goorm.domain.cloth.domain.repository.ClothImageRepository;
import study.goorm.domain.cloth.domain.repository.ClothRepository;
import study.goorm.domain.folder.domain.repository.FolderRepository;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.member.domain.repository.MemberRepository;
import study.goorm.domain.member.domain.exception.MemberException;
import study.goorm.domain.cloth.domain.repository.CategoryRepository;
import study.goorm.domain.model.enums.ClothSort;
import study.goorm.global.error.code.status.ErrorStatus;
import study.goorm.domain.folder.domain.repository.ClothFolderRepository;
import study.goorm.domain.history.domain.repository.HistoryClothRepository

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

    private final CategoryRepository categoryRepository;

    // 옷 추가
    @Override
    @Transactional
    public ClothResponseDTO.ClothCreateResult createCloth(ClothRequestDTO.ClothCreateRequest clothCreateResult, MultipartFile image) {

        Member member = memberRepository.findById(clothCreateResult.getMemberId())
                .orElseThrow(()-> new MemberException(ErrorStatus.NO_SUCH_MEMBER));

        Category category = categoryRepository.findById(clothCreateResult.getCategoryId())
                .orElseThrow(()-> new ClothException(ErrorStatus.NO_SUCH_CATEGORY));

        Cloth newCloth = Cloth.builder()
                .name(clothCreateResult.getName())
                .wearNum(0)
                .season(clothCreateResult.getSeasons())
                .tempUpperBound(clothCreateResult.getTempUpperBound())
                .tempLowerBound(clothCreateResult.getTempLowerBound())
                .thicknessLevel(clothCreateResult.getThicknessLevel())
                .clothUrl(clothCreateResult.getClothUrl())
                .brand(clothCreateResult.getBrand())
                .category(category)
                .member(member)
                .build();

        clothRepository.save(newCloth);

        ClothImage newClothImage = ClothImage.builder()
                .cloth(newCloth)
                .imageUrl("아직 S3를 구현하지 않아서 url이 없어용")
                .build();

        clothImageRepository.save(newClothImage);

        return ClothConverter.toClothCreateResult(newCloth);
    }

    private final ClothFolderRepository clothFolderRepository;
    private final HistoryClothRepository historyClothRepository;

    // 옷 삭제
    @Override
    @Transactional
    public void deleteCloth(Long clothId) {

        Cloth cloth = clothRepository.findById(clothId)
                .orElseThrow(()-> new ClothException(ErrorStatus.NO_SUCH_CLOTH));

        //매핑 테이블 삭제
        clothImageRepository.deleteAllByCloth(cloth);
        clothFolderRepository.deleteAllByCloth(cloth);
        historyClothRepository.deleteAllByCloth(cloth);

        //최종 옷 삭제
        clothRepository.delete(cloth);
    }
}
