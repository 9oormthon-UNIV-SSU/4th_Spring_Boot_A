package study.goorm.domain.member.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikedMemberDTO {
    private Long memberId;
    private String clokeyId;
    private String imageUrl;
    private String nickname;
    Boolean isFollowed;
    Boolean isMe;
}
