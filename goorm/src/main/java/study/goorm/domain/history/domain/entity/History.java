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
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class History extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate historyDate;

    @Min(0) // 0이상만 허용 (유효성 검사)
    @Column(nullable = false, columnDefinition = "integer default 0") // DB 저장 시 기본 값 0으로 설정)
    private int likes;

    @Column(length = 100)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
