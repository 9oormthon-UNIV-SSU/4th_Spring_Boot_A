package study.goorm.domain.cloth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import study.goorm.domain.cloth.application.ClothService;
import study.goorm.domain.cloth.dto.ClothResponseDTO;
import study.goorm.domain.model.enums.ClothSort;
import study.goorm.global.common.response.BaseResponse;
import study.goorm.global.error.code.status.SuccessStatus;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cloth-")
@Validated
public class ClothRestController {

    private final ClothService clothService;

    @GetMapping("/{cloth-id}/edit-view")
    @Operation(summary = "특정 Cloth에 대한 정보를 수정용으로 조회하는 API", description = "Path Variable로 clothId를 던져주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CLOTH_200", description = "OK, 성공적으로 조회되었습니다."),
    })
    public BaseResponse<ClothResponseDTO.ClothEditViewResult> getClothEditView(
            @PathVariable(name = "cloth-id") Long clothId
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
            @RequestParam int page,
            @RequestParam int size
    ) {

        ClothResponseDTO.MemberClosetResult result = clothService.getMemberCloset(clokeyId,sort,page,size);

        return BaseResponse.onSuccess(SuccessStatus.CLOTH_VIEW_SUCCESS, result);
    }
}

