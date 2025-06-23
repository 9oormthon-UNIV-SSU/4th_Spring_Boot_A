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
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id", nullable = false)
    private History history;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment comment;

    public boolean isOwnedBy(Member member) {
        // 현재 기록의 주인이 없거나, 비교 대상 멤버가 없으면 false
        if (this.member == null || member == null) {
            return false;
        }
        // Member 객체끼리 비교 (Member 클래스에 equals가 id 기준으로 구현되어 있어야 함)
        return this.member.equals(member);
    }

    public void setContent(String content) {
        this.content = content;
    }
}
