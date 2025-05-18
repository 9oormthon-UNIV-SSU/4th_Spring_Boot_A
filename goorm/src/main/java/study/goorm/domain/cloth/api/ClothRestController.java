package study.goorm.domain.cloth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study.goorm.domain.cloth.application.ClothService;
import study.goorm.domain.cloth.dto.ClothResponseDTO;
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
}
