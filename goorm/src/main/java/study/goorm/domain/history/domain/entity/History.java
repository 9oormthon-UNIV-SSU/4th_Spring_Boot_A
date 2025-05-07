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
    private Long id; // 기록 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 사용자 ID

    @Column(nullable = false)
    private LocalDate historyDate; // 날짜

    @Min(0) // 0 이상만 허용 (유효성 검사)
    @Column(nullable = false, columnDefinition = "integer default 0") // DB 저장 시 기본 값 0으로 설정)
    private int likes; // 좋아요 수

    @Column(length = 100)
    private String content; // 내용


}
