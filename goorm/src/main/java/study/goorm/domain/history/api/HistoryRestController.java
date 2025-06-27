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
import study.goorm.domain.history.dto.HistoryRequestDTO;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.model.enums.ClothSort;
import study.goorm.domain.model.exception.annotation.CheckPage;
import study.goorm.domain.model.exception.annotation.CheckPageSize;
import study.goorm.global.common.response.BaseResponse;
import study.goorm.global.error.code.status.SuccessStatus;
import study.goorm.domain.history.application.HistoryService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/histories")
@Validated
public class HistoryRestController {
    private final HistoryService historyService;

    // 월별 기록 조회
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
            @RequestParam String month
    ) {
        HistoryResponseDTO.HistoryGetMonthly result = historyService.getHistoryGetMonthly(clokeyId, month);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_GET_MONTH, result);
    }

    // 일별 기록 조회
    @GetMapping("/{historyId}")
    @Operation(summary = "특정 회원의 특정 일의 기록을 확인할 수 있는 API", description = "path variable로 historyId를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200", description = "OK, 일별 기록이 성공적으로 조회되었습니다."),
    })
    @Parameters({
            @Parameter(name = "historyId", description = "기록의 id, path variable 입니다.")
    })
    public BaseResponse<HistoryResponseDTO.HistoryGetDaily> getDailyHistory(
            @PathVariable Long historyId
    ) {
        HistoryResponseDTO.HistoryGetDaily result = historyService.getHistoryGetDaily(historyId);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_GET_MONTH, result);
    }

    // 기록 삭제
    @DeleteMapping("/{historyId}")
    @Operation(summary = "특정 기록을 삭제하는 API", description = "path variable로 historyId를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_202", description = "OK, 성공적으로 삭제되었습니다."),
    })
    @Parameters({
            @Parameter(name = "historyId", description = "기록의 id, path variable 입니다.")
    })
    public BaseResponse<Void> deleteHistory(
            @PathVariable(value = "historyId") Long historyId
    ) {

        historyService.deleteHistory(historyId);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_DELETED, null);
    }

    // 옷 기록 추가
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "새로운 옷 기록을 생성하는 API", description = "request body에 HistoryCreateRequest 형식의 데이터를 전달해주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_201", description = "CREATED, 성공적으로 생성되었습니다."),
    })
    public BaseResponse<HistoryResponseDTO.HistoryCreateResult> createHistory(
            @RequestPart("historyCreateRequest") HistoryRequestDTO.HistoryCreateRequest historyCreateRequest,
            @RequestPart("imageFile") List<MultipartFile> imageFile
    ) {
        HistoryResponseDTO.HistoryCreateResult result = historyService.createHistory(historyCreateRequest,imageFile);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_CREATED, result);
    }

    @PatchMapping(value = "/{historyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "특정날짜 옷 기록을 수정하는 API", description = "path variable로 historyId를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200", description = "OK, 기록이 성공적으로 수정되었습니다."),
    })
    @Parameters({
            @Parameter(name = "historyId", description = "기록의 id, path variable 입니다."),
    })
    public BaseResponse<ClothResponseDTO.ClothCreateResult> patchHistory(
            @PathVariable Long historyId,
            @RequestPart("historyUpdateRequest") @Valid HistoryRequestDTO.HistoryUpdateRequest historyUpdateRequest,
            @RequestPart("imageFile") List<MultipartFile> imageFile
    ) {
        HistoryResponseDTO.HistoryUpdateResult result = historyService.updateHistory(historyUpdateRequest, imageFile, historyId);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_UPDATED, null);
    }

    // 좋아요 기능
    @PostMapping("/like")
    @Operation(summary = "특정 기록에 좋아요를 누를 수 있는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200", description = "좋아요 상태가 성공적으로 변경되었습니다."),
    })
    public BaseResponse<HistoryResponseDTO.HistoryLikeResult> like(
            Member member,
            @RequestBody @Valid HistoryRequestDTO.HistoryLike historyLike
    ) {
        //isLiked의 상태에 따라서 좋아요 -> 취소 , 좋아요가 없는 상태 -> 좋아요 로 바꿔주게 됩니다.
        HistoryResponseDTO.HistoryLikeResult result = historyService.changeLikeStatus(member.getId(), historyLike.getHistoryId(), historyLike.isLiked());

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_LIKE_STATUS_CHANGED, result);
    }

    // 좋아요 누른 유저 조회
    @GetMapping("/{historyId}/likes")
    @Operation(summary = "특정 기록에 좋아요를 누른 유저의 정보를 확인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200", description = "기록의 좋아요를 누른 유저 정보를 성공적으로 조회했습니다."),
    })
    @Parameters({
            @Parameter(name = "historyId", description = "기록의 id, path variable 입니다.")
    })
    public BaseResponse<HistoryResponseDTO.HistoryLikedUserResultList> getLikedUsers(
            Member member,
            @PathVariable Long historyId
    ) {

        HistoryResponseDTO.HistoryLikedUserResultList result = historyService.getLikedUsers(member.getId(), historyId);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_LIKE_USER, result);
    }

    // 댓글 추가
    @PostMapping("/{historyId}/comments")
    @Operation(summary = "댓글을 남길 수 있는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_201", description = "성공적으로 댓글이 생성되었습니다."),
    })
    @Parameters({
            @Parameter(name = "historyId", description = "댓글을 남기고자 하는 기록의 ID")
    })
    public BaseResponse<HistoryResponseDTO.HistoryCommentWriteResult> writeComments(
            @PathVariable @Valid Long historyId,
            @RequestBody @Valid HistoryRequestDTO.HistoryCommentWrite request,
            Member member
    ) {
        return BaseResponse.onSuccess(SuccessStatus.HISTORY_COMMENT_CREATED, historyService.writeComment(historyId, request.getCommentId(), member.getId(), request.getContent()));

    }

    // 댓글 조회
    @GetMapping("/{historyId}/comments")
    @Operation(summary = "특정 기록의 댓글을 읽어올 수 있는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200", description = "OK, 성공적으로 조회되었습니다.")
    })
    @Parameters({
            @Parameter(name = "historyId", description = "기록의 id, path variable 입니다."),
            @Parameter(name = "page", description = "페이징 관련 query parameter")

    })
    public BaseResponse<HistoryResponseDTO.HistoryCommentResult> getComments(
            @PathVariable @Valid Long historyId,
            @RequestParam(value = "page") @Valid @CheckPage int page
    ) {

        HistoryResponseDTO.HistoryCommentResult result = historyService.getComments(historyId, page - 1);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_SUCCESS, result);
    }

    @DeleteMapping(value = "/comments/{commentId}")
    @Operation(summary = "댓글을 삭제하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_204", description = "댓글이 성공적으로 삭제되었습니다."),
    })
    @Parameters({
            @Parameter(name = "commentId", description = "삭제하고자 하는 댓글의 ID")
    })
    public BaseResponse<Void> deleteComment(
            Member member,
            @PathVariable Long commentId
    ) {

        historyService.deleteComment(commentId, member.getId());

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_COMMENT_DELETED, null);
    }

    @PatchMapping(value = "/comments/{commentId}")
    @Operation(summary = "댓글을 수정하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_204", description = "댓글이 성공적으로 수정되었습니다."),
    })
    @Parameters({
            @Parameter(name = "commentId", description = "수정하고자 하는 댓글의 ID")
    })
    public BaseResponse<Void> updateComment(
            @RequestBody @Valid HistoryRequestDTO.HistoryUpdateComment updateCommentRequest,
            @PathVariable Long commentId,
            Member member
    ) {

        historyService.updateComment(updateCommentRequest, commentId, member.getId());

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_COMMENT_UPDATED, null);
    }

}
