package study.goorm.domain.history.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import study.goorm.domain.history.application.HistoryService;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.global.common.response.BaseResponse;
import study.goorm.global.error.code.status.SuccessStatus;

@RestController
@RequiredArgsConstructor
@RequestMapping("/histories")
public class HistoryRestController {

    private final HistoryService historyService;

    @GetMapping("/monthly/")
    @Operation(summary = "특정 월의 기록을 전부 조회하는 API", description = "query string으로 clokeyId, month를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse( responseCode = "HISTORY_200", description = "OK, 성공적으로 조회되었습니다.")
    })
    @Parameters({
            @Parameter(name = "clokey-id", description = "클로키 유저의 clokey id, query string 입니다."),
            @Parameter(name = "month", description = "YYYY-MM 형태로 값을 입력 받는 query string 입니다.")
    })
    public BaseResponse<HistoryResponseDTO.HistoryMonthResult> getMonthlyHistories(
            @RequestParam(value = "clokey-id") String clokeyId,
            @RequestParam(value = "month") String month
    ) {

        HistoryResponseDTO.HistoryMonthResult result = historyService.getMonthlyHistories(clokeyId,month);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_MONTH, result);
    }


}
