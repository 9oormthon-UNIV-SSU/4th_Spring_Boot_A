package study.goorm.domain.cloth.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import study.goorm.domain.model.entity.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ClothImage extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 옷 사진 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cloth_id", nullable = false)
//    @JoinColumn(name = "cloth_id", nullable = false, unique = true)
    private Cloth cloth;

    @Column(nullable = false)
    private String imageUrl;
}
