package study.goorm.domain.cloth.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import study.goorm.domain.model.entity.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Category extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 카테고리 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id") // null -> 최상위 카테고리 / notnull -> 하위
//    @JoinColumn(name = "parent_id", nullable = false)
    private Category category; // 상위 카데고리 ID

    @Column(nullable = false, length = 50)
    private String name; // 카테고리 이름
}
