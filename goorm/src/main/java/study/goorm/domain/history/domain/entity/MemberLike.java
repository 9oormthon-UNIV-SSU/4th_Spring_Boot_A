package study.goorm.domain.history.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.model.entity.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "history_id"})) // 복합 유니크 키 설정 -> 한 회원이 게시물에 중복해서 좋아요를 누르지 못하도록 제약설정한 것
public class MemberLike extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 좋아요 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 사용자 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id", nullable = false)
    private History history; // 기록 ID
}
