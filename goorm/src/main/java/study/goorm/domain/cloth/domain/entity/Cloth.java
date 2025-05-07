package study.goorm.domain.cloth.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.model.entity.BaseEntity;
import study.goorm.domain.model.enums.Season;
import study.goorm.domain.model.enums.ThicknessLevel;

import java.util.List;

@Entity
@Getter
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Cloth extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 옷 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 유저 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; // 카테고리 ID

    @Column(nullable = false, length = 50)
    private String name; // 이름

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int wearNum; // 착용횟수

    @ElementCollection(targetClass = Season.class) // 값을 여러개 저장
    @CollectionTable(name = "cloth_seasons", joinColumns = @JoinColumn(name = "cloth_id")) //
    @Enumerated(EnumType.STRING)
    @Column(name = "season", nullable = false)
    private List<Season> season; // 계절 리스트(복수선택 가능)

    @Min(-20)
    @Max(40)
    @Column(nullable = false)
    private int tempUpperBound; // 상한온도

    @Min(-20)
    @Max(40)
    @Column(nullable = false)
    private int tempLowerBound; // 하한 온도

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ThicknessLevel thicknessLevel; // 두께감

    private String clothUrl; // 상품정보 url

    private String brand; // 브렌드

}
