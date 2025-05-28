package study.goorm.domain.history.domain.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import study.goorm.domain.history.domain.application.HistoryService;
import study.goorm.domain.history.domain.dto.HistoryRequestDTO;
import study.goorm.domain.history.domain.dto.HistoryResponseDTO;
import study.goorm.global.common.response.BaseResponse;
import study.goorm.global.error.code.status.SuccessStatus;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/history-")
@Validated
public class HistoryRestController {
    private final HistoryService historyService;

    @GetMapping("/monthly/")
    @Operation( summary = "월별 기록 조회 API", description = "query string으로 clokeyid, month 넣어줘야합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse( responseCode = "HISTORY_200", description = "월별 기록이 성공적으로 조회되었습니다.")
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


    @GetMapping("/{historyId}")
    @Operation(summary = "일별 기록을 조회하는 API", description = "historyId를 Path Variable입력하면 조회가 가능합니다.")
    @Parameter(name = "historyId", description = "조회하고자 하는 기록의 ID", required = true)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200", description = "성공적으로 조회되었습니다.")
    })
    public BaseResponse<HistoryResponseDTO.DailyHistoryResult> getDailyHistory(
            @PathVariable(name = "historyId") Long historyId
    ){
        HistoryResponseDTO.DailyHistoryResult result = historyService.getDailyHistory(historyId);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_VIEW_SUCCESS, result);
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "날짜별 옷 기록 추가 API", description = "해당 날짜에 기록을 추가하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_201", description = "CREATED, 성공적으로 생성되었습니다.")
    })
    public BaseResponse<HistoryResponseDTO.HistoryCreateResult> createHistory(
            @RequestPart("historyCreateRequest") @Valid HistoryRequestDTO.HistoryCreateRequest historyCreateRequest,
            @RequestPart("imageFile") List<MultipartFile> imageFiles
    ){
        HistoryResponseDTO.HistoryCreateResult result = historyService.createHistory(historyCreateRequest, imageFiles);
        return BaseResponse.onSuccess(SuccessStatus.HISTORY_CREATED, result);
    }


    @PatchMapping(value = "/{historyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "날짜별 옷 기록 수정 API", description = "기록 ID로 기존 기록을 수정합니다.")
    public BaseResponse<Void> updateHistory(
            @PathVariable Long historyId,
            @RequestPart("metadata") @Valid HistoryRequestDTO.HistoryUpdateRequest metadata,
            @RequestPart("image") List<MultipartFile> imageFiles
    ) {
        historyService.updateHistory(historyId, metadata, imageFiles);
        return BaseResponse.onSuccess(SuccessStatus.HISTORY_DELETED, null);
    }

    @DeleteMapping("/{historyId}")
    @Operation(summary = "날짜별 옷 기록 삭제 API", description = "해당 ID의 기록을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "HISTORY_200", description = "기록이 성공적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "HISTORY_4002", description = "존재하지 않는 기록 ID 입니다.")
    })
    public BaseResponse<Void> deleteHistory(@PathVariable Long historyId) {
        historyService.deleteHistory(historyId);
        return BaseResponse.onSuccess(SuccessStatus.HISTORY_DELETED, null);
    }


}
