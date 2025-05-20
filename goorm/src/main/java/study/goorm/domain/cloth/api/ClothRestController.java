package study.goorm.domain.cloth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import study.goorm.domain.cloth.application.ClothService;
import study.goorm.domain.cloth.dto.ClothRequestDTO;
import study.goorm.domain.cloth.dto.ClothResponseDTO;
import study.goorm.domain.model.annotation.CheckPage;
import study.goorm.domain.model.annotation.CheckPageSize;
import study.goorm.domain.model.enums.ClothSort;
import study.goorm.global.common.response.BaseResponse;
import study.goorm.global.error.code.status.SuccessStatus;


@RestController
@RequiredArgsConstructor
@RequestMapping("/cloth")
@Validated
public class ClothRestController {

    private final ClothService clothService;

    @GetMapping("/{cloth-id}/edit-view")
    @Operation(summary = "특정 Cloth에 대한 정보를 수정용으로 조회하는 API", description = "Path Variable로 clothId를 던져주세요.")
    @ApiResponses({ // 이 API에서 나올 수 있는 ApiResponse에 대해서 적어주시면 됩니다. (제거해도 되고 성공, 실패 response 모두 작성 가능)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CLOTH_200", description = "OK, 성공적으로 조회되었습니다."),
    })
    public BaseResponse<ClothResponseDTO.ClothEditViewResult> getClothEditView(
            @PathVariable(name = "cloth-id") Long clothId // PathVariable을 받겠다는 어노테이션
            // name 옵션을 통해서 받아서 카멜 케이스에 어울리도록 clothId로 받아줍니다.
    ) {
        ClothResponseDTO.ClothEditViewResult result = clothService.getClothEditView(clothId);

        return BaseResponse.onSuccess(SuccessStatus.CLOTH_VIEW_SUCCESS, result);
    }

    @GetMapping("/closet-view")
    @Operation( summary = "유저의 옷장을 조회하는 API", description = "query string으로 sort, page, size를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse( responseCode = "CLOTH_200", description = "OK, 성공적으로 조회되었습니다.")
    })
    @Parameters({
            @Parameter(name = "clokey-id", description = "클로키 유저의 clokey id, query string 입니다."),
            @Parameter(name = "sort", description = "정렬(Sort) ENUM 값 { WEAR, NOT_WEAR, LATEST, OLDEST }, query string 입니다."),
            @Parameter(name = "page", description = "페이지 값, query string 입니다."),
            @Parameter(name = "size", description = "페이지에 표시할 요소 개수 값, query string 입니다.")
    })
    public BaseResponse<ClothResponseDTO.MemberClosetResult> getMemberCloset(
            @RequestParam(value = "clokey-id") String clokeyId,
            @RequestParam ClothSort sort,
            @RequestParam @CheckPage int page,
            @RequestParam @CheckPageSize int size
    ) {

        ClothResponseDTO.MemberClosetResult result = clothService.getMemberCloset(clokeyId,sort,page,size);

        return BaseResponse.onSuccess(SuccessStatus.CLOTH_VIEW_SUCCESS, result);
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "새로운 옷을 생성하는 API", description = "request body에 ClothCreateRequest 형식의 데이터를 전달해주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CLOTH_201", description = "CREATED, 성공적으로 생성되었습니다."),
    })
    public BaseResponse<ClothResponseDTO.ClothCreateResult> createCloth(
            @RequestPart("clothCreateRequest") @Valid ClothRequestDTO.ClothCreateRequest clothCreateRequest,
            @RequestPart("imageFile") MultipartFile imageFile
    ) {
        ClothResponseDTO.ClothCreateResult result = clothService.createCloth(clothCreateRequest,imageFile);

        return BaseResponse.onSuccess(SuccessStatus.CLOTH_CREATED, result);
    }

    @DeleteMapping("/{cloth-id}")
    @Operation(summary = "특정 옷을 삭제하는 API", description = "path variable로 cloth_id를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CLOTH_204", description = "OK, 성공적으로 삭제되었습니다."),
    })
    @Parameters({
            @Parameter(name = "cloth-id", description = "옷의 id, path variable 입니다.")
    })
    public BaseResponse<Void> deleteCloth(
            @PathVariable(value = "cloth-id") Long clothId
    ) {

        clothService.deleteCloth(clothId);

        return BaseResponse.onSuccess(SuccessStatus.CLOTH_DELETED, null);
    }
}
