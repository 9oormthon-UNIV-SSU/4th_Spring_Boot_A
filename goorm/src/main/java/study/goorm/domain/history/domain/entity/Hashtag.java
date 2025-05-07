package study.goorm.domain.history.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import study.goorm.domain.model.entity.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Hashtag extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 해시태그 ID

    @Column(nullable = false, length = 30, unique = true) // 중복된 해시태그 이름 허용하지 않기 위해 unique 설정
    private String name; // 이름
}

// 야호 ERD보고 씀 ~ ㅋㅋ.. 간단하긴함 -> 엥 unique설정 안해줌 ? ERD에 없었는디? -> DB 절약 위함