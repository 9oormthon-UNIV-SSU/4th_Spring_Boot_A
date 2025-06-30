package study.goorm.domain.history.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.model.entity.BaseEntity;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class History extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate historyDate;

    @Min(0)
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int likes;

    @Column(length = 200)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public boolean isOwnedBy(Member member) {
        // 방어 로직: 현재 기록의 주인이 없거나, 비교 대상 멤버가 없으면 false
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

