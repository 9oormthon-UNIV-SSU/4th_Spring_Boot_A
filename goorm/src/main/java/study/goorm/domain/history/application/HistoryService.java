package study.goorm.domain.history.application;

import org.springframework.web.multipart.MultipartFile;
import study.goorm.domain.history.dto.HistoryRequestDTO;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.member.domain.entity.Member;

import java.time.LocalDate;
import java.util.List;

public interface HistoryService {
    HistoryResponseDTO.HistoryGetMonthly getHistoryGetMonthly(String clokeyId, String month);
    HistoryResponseDTO.HistoryGetDaily getHistoryGetDaily(Long historyId);
    HistoryResponseDTO.HistoryCreateResult createHistory(HistoryRequestDTO.HistoryCreateRequest historyCreateResult, List<MultipartFile> image);
    HistoryResponseDTO.HistoryUpdateResult updateHistory(HistoryRequestDTO.HistoryUpdateRequest historyUpdateRequest, List<MultipartFile> imageFile, Long historyId);
    void deleteHistory(Long historyId);

    HistoryResponseDTO.HistoryLikeResult changeLikeStatus(Long memberId, Long historyId, boolean isLiked);
    HistoryResponseDTO.HistoryLikedUserResultList getLikedUsers(Long memberId, Long historyId);
    HistoryResponseDTO.HistoryCommentWriteResult writeComment(Long historyId, Long commentId, Long memberId, String content);
    HistoryResponseDTO.HistoryCommentResult getComments(Long historyId, int page);
    void deleteComment(Long commentId, Long memberId);
    void updateComment(HistoryRequestDTO.HistoryUpdateComment updateCommentRequest, Long commentId, Long memberId);
}
