package study.goorm.domain.history.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import study.goorm.domain.history.application.HistoryService;
import study.goorm.domain.history.dto.HistoryRequestDTO;
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

    @GetMapping("/{historyId}")
    @Operation(summary = "특정 History에 대한 정보를 조회하는 API", description = "Path Variable로 historyId를 던져주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_201", description = "OK, 성공적으로 조회되었습니다."),
    })
    public BaseResponse<HistoryResponseDTO.HistoryDayResult> getDailyHistory(
            @Parameter(name = "historyId") Long historyId
    ) {
        HistoryResponseDTO.HistoryDayResult result = historyService.getDailyHistory(historyId);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_DAY, result);
    }


    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "새로운 기록 생성 API", description = "메타데이터(JSON)와 이미지 파일을 받아 기록을 생성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_202", description = "OK, 성공적으로 조회되었습니다.")
    })
    public BaseResponse<Long> createHistory(
            @RequestPart("metadata") HistoryRequestDTO.HistoryCreateDTO request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            @RequestHeader(name = "clokey-id") String clokeyId // 사용자 인증용
    ) {
        Long historyId = historyService.createHistory(clokeyId, request, imageFile);
        return BaseResponse.onSuccess(SuccessStatus.HISTORY_CREATED, historyId);
    }

    @PatchMapping(value = "/{historyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "특정 History에 대한 정보를 수정하는 API", description = "Path Variable로 historyId를 던져주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_203", description = "OK, 성공적으로 조회되었습니다."),
    })
    public BaseResponse<Void> updateHistory(
            @PathVariable(name = "historyId") Long historyId,
            @RequestPart("metadata") HistoryRequestDTO.HistoryUpdateDTO request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            @RequestHeader(name = "clokey-id") String clokeyId
    ) {
        historyService.updateHistory(historyId, clokeyId, request, imageFile);

        return BaseResponse.onSuccess(SuccessStatus.CLOTH_DELETED, null);
    }

    @DeleteMapping("/{historyId}")
    @Operation(summary = "특정 History 기록을 삭제하는 API", description = "Path Variable로 historyId를 던져주세요. " +
            "관련된 댓글, 옷 착용 횟수, 해시태그 기록, 사진, 좋아요, 기록-옷 연결이 모두 삭제됩니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "NO_CONTENT, 성공적으로 삭제되었습니다."),
    })
    public BaseResponse<Void> deleteHistory(
            @PathVariable Long historyId,
            @RequestHeader(name = "clokey-id") String clokeyId
    ) {
        historyService.deleteHistory(historyId, clokeyId);

        return BaseResponse.onSuccess(SuccessStatus.CLOTH_DELETED, null);
    }
}
