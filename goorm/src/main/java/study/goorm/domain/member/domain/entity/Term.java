package study.goorm.domain.member.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import study.goorm.domain.model.entity.BaseEntity;

@Entity // JPA 엔티티임을 선언 -> DB 테이블로 매핑되고 클래스 이름이 테이블 이름이 되며 따로 지정하지 않으면 term으로 자동 생성됨
@Getter // Lombok 애너테이션 -> 모든 필드에 대한 getId(), getTitle() 등 getter 매서드 자동 생성
@Builder // 빌더 패턴 지원 -> 객체 생성 시 유연하게 사용 가능
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 자동 생성(인자없는 생성자) + protect 접근 제한 -> 외부에서 실수로 직접 생성 못하게 막음
@AllArgsConstructor // 모든 필드 포함한 전체 인자 생성자 자동 생성
public class Term extends BaseEntity { // BaseEntity 상속받음
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK 키 설정
    private Long id; // 약관ID

    @Column(nullable = false) // NOT NUll 설정해줌
    private String title; // 제목

    @Column(nullable = false)
    private String body; // 내용

    @Column(nullable = false)
    private Boolean optional; // 필수 여부(선택 약관)

}
