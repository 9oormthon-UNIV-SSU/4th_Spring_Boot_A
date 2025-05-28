package study.goorm.domain.history.domain.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import study.goorm.domain.cloth.domain.application.ClothService;
import study.goorm.domain.history.domain.application.HistoryService;
import study.goorm.domain.history.domain.dto.HistoryResponseDTO;
import study.goorm.global.common.response.BaseResponse;
import study.goorm.global.error.code.status.SuccessStatus;

import java.time.LocalDateTime;
import java.time.YearMonth;

@RestController
@RequiredArgsConstructor
@RequestMapping("/history-")
@Validated
public class HistoryRestController {
    private final HistoryService historyService;

    @GetMapping("/monthly/")
    @Operation( summary = "월별 기록 조회 API", description = "query string으로 clokeyid, month 넣어줘야합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse( responseCode = "HISTORY_200", description = "OK, 성공적으로 조회되었습니다.")
    })
    @Parameters({
            @Parameter(name = "clokey-id", description = "클로키 유저의 clokey id, 입력하지 않으면 본인의 월별 기록을 확인합니다."),
            @Parameter(name = "month", description = "날짜 형식은 YYYY-MM(ex.2025-01)과 같은 형태로 입력해야합니다.")
    })
    public BaseResponse<HistoryResponseDTO.MonthlyHistoriesResult> getMonthlyHistories(
            @RequestParam(value = "clokey-id", required = false) String clokeyId, // required false -> 값 안넣어도 되지만 안넣으면 null로 돌어옴
            @RequestParam("month") @DateTimeFormat(pattern = "YYYY-MM") YearMonth month
            ){
        HistoryResponseDTO.MonthlyHistoriesResult result = historyService.getMonthlyHistories(clokeyId, month);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_VIEW_SUCCESS, result);
    }





}
