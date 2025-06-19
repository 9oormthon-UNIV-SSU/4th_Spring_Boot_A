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

import study.goorm.domain.cloth.dto.ClothResponseDTO;
import study.goorm.domain.history.application.HistoryService;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.history.dto.HistoryRequestDTO;
import study.goorm.global.common.response.BaseResponse;
import study.goorm.global.error.code.status.SuccessStatus;

import java.util.List;

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
            @Parameter(name = "month", description = "기록을 조회할 월(month)입니다. (YYYY-MM)형식")
    })
    public BaseResponse<HistoryResponseDTO.MonthlyHistoryResult> getMonthlyHistory(
            @RequestParam(value = "clokey-id", required = false) String clokeyId,
            @RequestParam String month
    ) {
        HistoryResponseDTO.MonthlyHistoryResult result = historyService.getMonthlyHistory(clokeyId, month);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_MONTHLY_SUCCESS, result);
    }

    @GetMapping("/histories/{history-id}")
    @Operation(summary = "유저의 일별 기록을 조회하는 API", description = "Path Variable로 historyId를 던져주세요.")
    @ApiResponses({ // 이 API에서 나올 수 있는 ApiResponse에 대해서 적어주시면 됩니다. (제거해도 되고 성공, 실패 response 모두 작성 가능)
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HiSTORY_DAILY_200", description = "OK, 성공적으로 조회되었습니다."),
    })
    public BaseResponse<HistoryResponseDTO.DailyHistoryResult> getDailyHistory(
            @PathVariable(name = "history-id") Long historyId // PathVariable을 받겠다는 어노테이션
            // name 옵션을 통해서 받아서 카멜 케이스에 어울리도록 clothId로 받아줍니다.
    ) {
        HistoryResponseDTO.DailyHistoryResult result = historyService.getDailyHistory(historyId);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_DAILY_SUCCESS, result);
    }

    @PostMapping(value = "/histories", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "새로운 기록을 생성하는 API", description = "request body에 HistoryCreateRequest 형식의 데이터를 전달해주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_201", description = "CREATED, 기록이 성공적으로 생성되었습니다."),
    })
    public BaseResponse<HistoryResponseDTO.HistoryCreateResult> createHistory(
            @RequestPart("historyCreateRequest") @Valid HistoryRequestDTO.HistoryCreateRequest historyCreateRequest,
            @RequestPart("imageFile") List<MultipartFile> imageFiles
    ) {
        HistoryResponseDTO.HistoryCreateResult result = historyService.createHistory(historyCreateRequest, imageFiles);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_CREATED, result);
    }


    @DeleteMapping("/histories/{history-id}")
    @Operation(summary = "날짜별 옷 기록을 삭제하는 API", description = "path variable로 history_id를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "History_204", description = "OK, 기록이 성공적으로 삭제되었습니다."),
    })
    @Parameters({
            @Parameter(name = "history-id", description = "기록의 id, path variable 입니다.")
    })
    public BaseResponse<Void> deleteHistory(
            @PathVariable(value = "history-id") Long historyId
    ) {

        historyService.deleteHistory(historyId);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_DELETED, null);
    }

    @PatchMapping(path = "/histories/{history-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "기록을 수정하는 API", description = "request body에 HistoryPatchRequest 형식의 데이터를 전달해주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_201", description = "CREATED, 기록이 성공적으로 생성되었습니다."),
    })
    public BaseResponse<HistoryResponseDTO.HistoryCreateResult> patchHistory(
            @RequestPart("historyPatchRequest") @Valid HistoryRequestDTO.HistoryPatchRequest historyPatchRequest,
            @RequestPart("imageFile") List<MultipartFile> imageFiles,
            @PathVariable(value = "history-id") Long historyId
    ) {
        //HistoryResponseDTO.HistoryPatchResult result = historyService.patchHistory(historyPatchRequest, imageFiles, historyId);

        historyService.patchHistory(historyPatchRequest, imageFiles, historyId);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_CREATED, null);
    }
}
