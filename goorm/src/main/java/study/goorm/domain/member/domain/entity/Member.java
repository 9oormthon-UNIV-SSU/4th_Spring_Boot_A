package study.goorm.domain.member.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import study.goorm.domain.model.entity.BaseEntity;
import study.goorm.domain.model.enums.SocialType;
import study.goorm.domain.model.enums.MemberStatus;


@Entity
@Getter
@Setter // setter 자동 생성
@DynamicUpdate // UPDATE 시 실제로 변경된 필드만 SQL에 포함
@DynamicInsert // INSERT 시 null이 아닌 필드만 SQL에 포함
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 사용자ID

    @Column(nullable = false, length = 50)
    private String email; // 이메일 주소

    @Column(unique = true, length = 50) // unique 설정, null 값허용
    private String clokeyId; // 클로키 아이디

    @Column(length = 20) // 글자수 제한 / notion에서는 30으로 나와있긴했음
    private String nickname; // 닉네임

    @Enumerated(EnumType.STRING) // SNS가입종류
    @Column(nullable = false)
    private SocialType socialType;

    private String profileImageUrl; // 프로필 사진 url

    private String profileBackImageUrl; // 배경사진url

    @Enumerated(EnumType.STRING) // 활성화여부
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'ACTIVE'", nullable = false)
    private  MemberStatus status;

    @Column(length = 50)
    private String bio; // 한줄소개

}
