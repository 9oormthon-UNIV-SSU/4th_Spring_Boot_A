package study.goorm.domain.history.converter;

import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryImage;
import study.goorm.domain.history.dto.HistoryRequestDTO;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.member.domain.dto.LikedMemberDTO;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.cloth.domain.entity.Cloth;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryConverter {

    public static HistoryResponseDTO.HistoryGetMonthly toHistoryGetMonthly(Member member, List<HistoryResponseDTO.HistoryGetMonthlyResult> result) {

        return HistoryResponseDTO.HistoryGetMonthly.builder()
                .memberId(member.getId())
                .nickName(member.getNickname())
                .histories(result)
                .build();
    }

    public static HistoryResponseDTO.HistoryGetDaily toHistoryGetDaily(History history, List<String> images, Member member, List<String> hashtags, List<HistoryResponseDTO.HistoryGetDailyCloth> cloths){
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

    public static HistoryResponseDTO.HistoryCreateResult toHistoryCreateResult(History history){
        return HistoryResponseDTO.HistoryCreateResult.builder()
                .historyId(history.getId())
                .build();
    }

    public static HistoryResponseDTO.HistoryUpdateResult toHistoryUpdateResult(History history){
        return HistoryResponseDTO.HistoryUpdateResult.builder()
                .historyId(history.getId())
                .build();
    }

    public static HistoryResponseDTO.HistoryLikeResult toHistoryLikeResult(History history, boolean isLiked){
        return HistoryResponseDTO.HistoryLikeResult.builder()
                .historyId(history.getId())
                .liked(!isLiked)
                .likeCount(history.getLikes())
                .build();
    }

    public static HistoryResponseDTO.HistoryLikedUserResultList toLikedUserResult(List<LikedMemberDTO> likedMembers){
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
}
