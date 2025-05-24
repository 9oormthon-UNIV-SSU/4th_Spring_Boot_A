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
import study.goorm.global.common.response.BaseResponse;
import study.goorm.global.error.code.status.SuccessStatus;

@RestController
@RequiredArgsConstructor
@RequestMapping("/history")
@Validated
public class HistoryRestController {
    private final HistoryService historyService;

    @GetMapping("/monthly")
    @Operation( summary = "유저의 월별 기록들을 조회하는 API", description = "query string으로 ClokryId, month를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse( responseCode = "HISTORY_MONTHLY_200", description = "OK, 성공적으로 조회되었습니다.")
    })
    @Parameters({
            @Parameter(name = "clokey-id", description = "클로키 유저의 clokey id, query string 입니다."),
            @Parameter(name = "month", description = "기록을 조회할 월(month)입니다. (YYYY-MM)")
    })
    public BaseResponse<ClothResponseDTO.MemberClosetResult> getMonthlyHistory(
            @RequestParam(value = "clokey-id") String clokeyId,
            @RequestParam String month
    ) {
        ClothResponseDTO.MemberClosetResult result = HistoryService.getMonthlyHistory(clokeyId, month);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_MONTHLY_SUCCESS, result);
    }

    @DeleteMapping("/{history-id}")
    @Operation(summary = "날짜별 옷 기록을 삭제하는 API", description = "path variable로 history_id를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "History_204", description = "OK, 기록이 성공적으로 삭제되었습니다."),
    })
    @Parameters({
            @Parameter(name = "history-id", description = "기록의 id, path variable 입니다.")
    })
    public BaseResponse<Void> delete(
            @PathVariable(value = "history-id") Long historyId
    ) {

        historyService.deleteHistory(historyId);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_DELETED, null);
    }
}
