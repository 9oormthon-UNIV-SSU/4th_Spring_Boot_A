package study.goorm.domain.history.api;

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
import study.goorm.domain.cloth.dto.ClothRequestDTO;
import study.goorm.domain.history.application.HistoryService;
import study.goorm.domain.history.dto.HistoryRequestDTO;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.global.common.response.BaseResponse;
import study.goorm.global.error.code.status.SuccessStatus;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/histories")
@Validated
public class HistoryRestController {

    private final HistoryService historyService;

    // 월별 기록 조회
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

    // 일별 기록 조회
    @GetMapping("/{history-id}")
    @Operation(summary = "유저의 일별 기록을 조회하는 API", description = "path variable를 통해 history-id 주세요")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200", description = "OK, 성공적으로 조회되었습니다."),
    })
    public BaseResponse<HistoryResponseDTO.DailyHistoryPreview> getDailyHistory(
            @PathVariable("history-id") Long historyId
    ) {
        HistoryResponseDTO.DailyHistoryPreview result = historyService.getDailyPreview(historyId);
        return BaseResponse.onSuccess(SuccessStatus.HISTORY_VIEW_SUCCESS, result);
    }

    // 날짜별 옷 기록 추가
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "유저의 날짜별 옷 기록을 추가하는 API", description = "request body에 HistoryCreateRequest 형식의 데이터를 전달해주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_201", description = "CREATED, 성공적으로 생성되었습니다.")
    })
    public BaseResponse<HistoryResponseDTO.HistoryCreateResult> createHistory(
            @RequestPart("historyCreateRequest") @Valid HistoryRequestDTO.HistoryCreateRequest historyCreateRequest,
            @RequestPart("imageFile") List<MultipartFile> imageFile
    ) {
        HistoryResponseDTO.HistoryCreateResult result = historyService.createHistory(historyCreateRequest, imageFile);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_CREATED, result);
    }

    // 날짜별 옷 수정
    @PatchMapping(value = "{history-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "유저의 날짜별 옷의 기록을 수정하는 API", description = "request body에 HistoryUpdateRequest 형식의 데이터를 전달해주시고, path variable을 통해 history-id를 던져주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200", description = "OK, 성공적으로 수정되었습니다."),
    })
    public BaseResponse<HistoryResponseDTO.HistoryUpdateResult> updateHistory(
            @RequestPart("historyUpdateRequest") @Valid HistoryRequestDTO.HistoryUpdateRequest historyUpdateRequest,
            @RequestPart("imageFile") List<MultipartFile> imageFile,
            @PathVariable("history-id") Long historyId
    ) {
        HistoryResponseDTO.HistoryUpdateResult result = historyService.updateHistory(historyUpdateRequest, imageFile, historyId);
        return BaseResponse.onSuccess(SuccessStatus.HISTORY_UPDATED, result);
    }

    @DeleteMapping("{history-id}")
    @Operation(summary = "유저의 날짜별 옷 기록을 삭제하는 API", description = "path variable로 history-id를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_202", description = "OK, 성공적으로 삭제되었습니다."),
    })
    @Parameters({
            @Parameter(name = "history-id", description = "기록 id, path variable 입니다.")
    })
    public BaseResponse<Void> deleteHistory(
            @PathVariable(value = "history-id") Long historyId
    ) {
        historyService.deleteHistory(historyId);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_DELETED, null);
    }
}
