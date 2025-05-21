package study.goorm.domain.cloth.domain.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study.goorm.domain.cloth.domain.application.ClothService;
import study.goorm.domain.cloth.domain.dto.ClothResponseDTO;
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
}