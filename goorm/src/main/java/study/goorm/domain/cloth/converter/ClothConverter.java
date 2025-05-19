package study.goorm.domain.cloth.converter;

import org.springframework.data.domain.Page;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.cloth.dto.ClothResponseDTO;
import study.goorm.domain.member.domain.entity.Member;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClothConverter {

    public static ClothResponseDTO.ClothEditViewResult toClothEditViewResult(Cloth cloth, String clothImageUrl){
        return ClothResponseDTO.ClothEditViewResult.builder()
                .id(cloth.getId())
                .brand(cloth.getBrand())
                .categoryId(cloth.getCategory().getId())
                .clothUrl(cloth.getClothUrl())
                .imageUrl(clothImageUrl)
                .name(cloth.getName())
                .seasons(cloth.getSeason())
                .tempLowerBound(cloth.getTempLowerBound())
                .tempUpperBound(cloth.getTempUpperBound())
                .thicknessLevel(cloth.getThicknessLevel())
                .build();
    }

    public static ClothResponseDTO.MemberClosetResult toMemberClosetResult(Member member, Map<Long,String> firstImagesOfCloth, Page<Cloth> clothes){
        return ClothResponseDTO.MemberClosetResult.builder()
                .nickName(member.getNickname())
                .clothPreviewListResult(toClothPreviewListResult(firstImagesOfCloth, clothes))
                .build();
    }

    private static ClothResponseDTO.ClothPreviewListResult toClothPreviewListResult(Map<Long, String> firstImagesOfCloth, Page<Cloth> clothes){
        return ClothResponseDTO.ClothPreviewListResult.builder()
                .clothPreviews(toClothPreview(firstImagesOfCloth, clothes))
                .isFirst(clothes.isFirst())
                .isLast(clothes.isLast())
                .totalElements(clothes.getTotalElements())
                .totalPage(clothes.getTotalPages())
                .build();
    }

    private static List<ClothResponseDTO.ClothPreview> toClothPreview(Map<Long, String> firstImagesOfCloth, Page<Cloth> clothes){
        return clothes.stream()
                .map(cloth -> ClothResponseDTO.ClothPreview.builder()
                        .id(cloth.getId())
                        .name(cloth.getName())
                        .wearNum(cloth.getWearNum())
                        .imageUrl(firstImagesOfCloth.get(cloth.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    public static ClothResponseDTO.ClothCreateResult toClothCreateResult(Cloth cloth){
        return ClothResponseDTO.ClothCreateResult.builder()
                .id(cloth.getId())
                .build();
    }
}
