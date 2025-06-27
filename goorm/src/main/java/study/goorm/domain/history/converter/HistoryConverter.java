package study.goorm.domain.history.converter;

import study.goorm.domain.history.domain.entity.Comment;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryImage;
import study.goorm.domain.history.dto.HistoryCommentParamDTO;
import study.goorm.domain.history.dto.HistoryRequestDTO;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.member.domain.dto.LikedMemberDTO;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.cloth.domain.entity.Cloth;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HistoryConverter {

    public static HistoryResponseDTO.HistoryGetMonthly toHistoryGetMonthly(Member member, List<HistoryResponseDTO.HistoryGetMonthlyResult> result) {

        return HistoryResponseDTO.HistoryGetMonthly.builder()
                .memberId(member.getId())
                .nickName(member.getNickname())
                .histories(result)
                .build();
    }

    public static HistoryResponseDTO.HistoryGetDaily toHistoryGetDaily(History history, List<String> images, Member member, List<String> hashtags, List<HistoryResponseDTO.HistoryGetDailyCloth> cloths) {
        return HistoryResponseDTO.HistoryGetDaily.builder()
                .memberId(member.getId())
                .historyId(history.getId())
                .memberImageUrl(member.getProfileImageUrl())
                .nickName(member.getNickname())
                .clokeyId(member.getClokeyId())
                .content(history.getContent())
                .imageUrl(images)
                .hashtags(hashtags) // 해시태그는 외부에서 받아서 설정
                .likeCount(history.getLikes()) // 기본값 또는 실제 값 추가 필요
                .commentCount(history.getComments()) // 기본값 또는 실제 값 추가 필요
                .date(history.getHistoryDate())
                .cloths(cloths) // 실제 의류 정보 변환
                .liked(false) // 실제 좋아요 여부는 사용자 컨텍스트 필요
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

    public static HistoryResponseDTO.HistoryLikeResult toHistoryLikeResult(History history, boolean isLiked) {
        return HistoryResponseDTO.HistoryLikeResult.builder()
                .historyId(history.getId())
                .liked(isLiked)
                .likeCount(history.getLikes())
                .build();
    }

    public static HistoryResponseDTO.HistoryLikedUserResultList toLikedUserResult(List<LikedMemberDTO> likedMembers) {
        List<HistoryResponseDTO.HistoryLikedUserResult> likedUserResults = new ArrayList<>();
        for (int i = 0; i < likedMembers.size(); i++) {
            LikedMemberDTO member = likedMembers.get(i);
            likedUserResults.add(HistoryResponseDTO.HistoryLikedUserResult.builder()
                    .clokeyId(member.getClokeyId())
                    .imageUrl(member.getImageUrl())
                    .followStatus(member.getIsFollowed())
                    .memberId(member.getMemberId())
                    .nickname(member.getNickname())
                    .isMe(member.getIsMyself())
                    .build());
        }
        return HistoryResponseDTO.HistoryLikedUserResultList.builder()
                .likedUsers(likedUserResults)
                .build();
    }

    public static HistoryResponseDTO.HistoryCommentWriteResult toCommentWriteResult(Comment comment) {
        return HistoryResponseDTO.HistoryCommentWriteResult.builder()
                .commentId(comment.getId())
                .build();
    }

    public static HistoryResponseDTO.HistoryCommentResult toHistoryCommentResult(
            List<HistoryCommentParamDTO> flatComments,
            int page,
            int pageSize,
            int totalRootCount
    ) {
        // 부모 댓글 id를 기준
        Map<Long, List<HistoryCommentParamDTO>> repliesGrouped = flatComments.stream()
                .filter(dto -> !dto.isRoot()) // 대댓글
                .collect(Collectors.groupingBy(HistoryCommentParamDTO::getParentId));

        // 댓글과 대댓글 연결해주기
        List<HistoryResponseDTO.CommentResult> rootResults = flatComments.stream()
                .filter(HistoryCommentParamDTO::isRoot)
                .map(root -> HistoryResponseDTO.CommentResult.builder()
                        .commentId(root.getCommentId())
                        .content(root.getContent())
                        .clokeyId(root.getClokeyId())
                        .nickName(root.getNickname())
                        .userImageUrl(root.getProfileImageUrl())
                        .replyResults(
                                repliesGrouped.getOrDefault(root.getCommentId(), List.of()).stream()
                                        .map(reply -> HistoryResponseDTO.ReplyResult.builder()
                                                .commentId(reply.getCommentId())
                                                .content(reply.getContent())
                                                .clokeyId(reply.getClokeyId())
                                                .nickName(reply.getNickname())
                                                .userImageUrl(reply.getProfileImageUrl())
                                                .build())
                                        .toList()
                        )
                        .build())
                .toList();

        int totalPage = (int) Math.ceil((double) totalRootCount / pageSize);
        int totalElements = rootResults.stream()
                .mapToInt(r -> 1 + (r.getReplyResults() != null ? r.getReplyResults().size() : 0))
                .sum();

        return HistoryResponseDTO.HistoryCommentResult.builder()
                .comments(rootResults)
                .totalPage(totalPage)
                .totalElements(totalElements)
                .isFirst(page == 0)
                .isLast(page + 1 == totalPage)
                .build();
    }
}
