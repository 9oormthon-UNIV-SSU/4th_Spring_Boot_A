package study.goorm.domain.member.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import study.goorm.domain.model.entity.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberTerm extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 사용자약관ID

    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계 설정 -> 여러 개가 하나의 Term에 연결될 수 있음
                                        // 객체를 바로 로딩하지 않고 나중에 실제 사용할 때 쿼리 날림
    @JoinColumn(name = "term_id", nullable = false) // DB 테이블에 term_id라는 외래키 컬럼 생성 + 반드시 Term이 존재해야한다는 필수관계
    private Term term; // 약관ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 유저ID

}
