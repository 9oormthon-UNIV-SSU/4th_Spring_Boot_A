package study.goorm.domain.history.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.model.entity.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Comment extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 댓글ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 사용자 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id", nullable = false)
    private History history; // 기록 ID

    @Column(nullable = false, length = 50)
    private String content; // 내용

    @ManyToOne(fetch = FetchType.LAZY) // 자기 자신 참조 관계 -> 부모 댓글
    @JoinColumn(name = "parent_id")// null -> 일반 댓글 // 부모 값 있으면 대댓글
    private Comment comment; // 부모 댓글 ID



}
