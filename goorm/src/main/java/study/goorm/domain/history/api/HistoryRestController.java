package study.goorm.domain.history.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import study.goorm.domain.cloth.dto.ClothResponseDTO;
import study.goorm.domain.history.application.HistoryService;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.model.enums.ClothSort;
import study.goorm.domain.model.exception.annotation.CheckPage;
import study.goorm.domain.model.exception.annotation.CheckPageSize;
import study.goorm.global.common.response.BaseResponse;
import study.goorm.global.error.code.status.SuccessStatus;
import study.goorm.domain.history.application.HistoryService;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/histories")
@Validated
public class HistoryRestController {
    private final HistoryService historyService;

    @GetMapping("/monthly")
    @Operation(summary = "특정 회원의 월별 기록을 조회하는 API", description = "query string으로 clokeyId, month를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200", description = "OK, 월별 기록이 성공적으로 조회되었습니다."),
    })
    @Parameters({
            @Parameter(name = "clokeyId", description = "클로키 유저의 clokey id, query string 입니다."),
            @Parameter(name = "month", description = "월별 값, query string 입니다.")
    })
    public BaseResponse<HistoryResponseDTO.HistoryGetMonthly> getMonthlyHistories(
            @RequestParam(value = "clokeyId") String clokeyId,
            @RequestParam LocalDate month
    ) {
        HistoryResponseDTO.HistoryGetMonthly result = historyService.getHistoryGetMonthly(clokeyId, month);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_GET_MONTH, result);
    }
}
