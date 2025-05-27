package study.goorm.domain.history.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import study.goorm.domain.history.application.HistoryService;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.global.common.response.BaseResponse;
import study.goorm.global.error.code.status.SuccessStatus;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/histories")
public class HistoryRestController {

    private final HistoryService historyService;

    @GetMapping("/monthly")
    @Operation(summary = "유저의 월별 기록을 조회하는 API", description = "query String을 통해 clokey-id, date를 주세요")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200", description = "OK, 성공적으로 조회되었습니다."),
    })
    @Parameters({
            @Parameter(name = "clokey-id", description = "클로키 유저의 clokey-id, query string 입니다."),
            @Parameter(name = "date", description = "조회 할 날짜의 query string 입니다.")
    })
    public BaseResponse<HistoryResponseDTO.MonthlyHistoryPreview> getMonthlyHistories(
            @RequestParam(value = "clokey-id") String clokeyId,
            @RequestParam String date
    ) {
        HistoryResponseDTO.MonthlyHistoryPreview result = historyService.getMonthlyPreview(clokeyId, date);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_VIEW_SUCCESS, result);
    }
}
