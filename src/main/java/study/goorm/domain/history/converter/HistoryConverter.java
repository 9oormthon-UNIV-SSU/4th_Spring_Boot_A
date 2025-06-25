package study.goorm.domain.history.converter;

import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.history.domain.entity.Comment;
import study.goorm.domain.history.domain.entity.Hashtag;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryImage;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.member.domain.entity.Member;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HistoryConverter {

    // 기록
    public static HistoryResponseDTO.MonthlyHistoryPreview toMonthlyHistoryPreview (
            Member member,
            List<History> histories,
            Map<Long, String> firstImagesOfHistory) {

        return HistoryResponseDTO.MonthlyHistoryPreview.builder()
                .memberId(member.getId())
                .nickName(member.getNickname())
                .histories(
                        histories.stream()
                                .map(history -> HistoryResponseDTO.MonthlyHistoryItemResult.builder()
                                        .historyId(history.getId())
                                        .date(history.getHistoryDate().toString())
                                        .imageUrl(firstImagesOfHistory.getOrDefault(history.getId(), "비공개입니다"))
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }

    public static HistoryResponseDTO.DailyHistoryPreview toDailyHistoryPreview(
            Member member,
            History history,
            List<String> images,
            List<String> hashtags,
            long commentCount,
            List<Cloth> cloths
    ) {

        return HistoryResponseDTO.DailyHistoryPreview.builder()
                .memberId(member.getId())
                .historyId(history.getId())
                .memberImageUrl(member.getProfileImageUrl())
                .nickName(member.getNickname())
                .clokeyId(member.getClokeyId())
                .contents(history.getContent())
                .imageUrl(images)
                .hashtags(hashtags)
                .likeCount(history.getLikes())
                .commentCount(commentCount)
                .date(history.getHistoryDate())
                .cloths(
                        cloths.stream()
                                .map(cloth -> HistoryResponseDTO.DailyHistoryClothesPreview.builder()
                                        .clothId(cloth.getId())
                                        .clothImageUrl(cloth.getClothUrl())
                                        .clothName(cloth.getName())
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }

    public static HistoryResponseDTO.HistoryCreateResult toHistoryCreateResult(History history) {
        return HistoryResponseDTO.HistoryCreateResult.builder()
                .historyId(history.getId())
                .build();
    }

    public static HistoryResponseDTO.HistoryUpdateResult toHistoryUpdateResult(History history) {
        return HistoryResponseDTO.HistoryUpdateResult.builder()
                .historyId(history.getId())
                .build();
    }

    // 좋아요 추가 / 삭제
    public static HistoryResponseDTO.HistoryLikeResult toHistoryLikeResult(History history, boolean isLiked) {
        return HistoryResponseDTO.HistoryLikeResult.builder()
                .historyId(history.getId())
                .isLiked(isLiked)
                .likeCount(history.getLikes())
                .build();
    }

    // 댓글 작성
    public static HistoryResponseDTO.CommentWriteResult toCommentWriteResult(Comment comment) {
        return HistoryResponseDTO.CommentWriteResult.builder()
                .commentId(comment.getId())
                .build();
    }
}
