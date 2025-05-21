package study.goorm.domain.cloth.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.cloth.domain.entity.ClothImage;

import java.util.List;

public interface ClothImageRepository extends JpaRepository<ClothImage, Long> {

    List<ClothImage> findAllByCloth(Cloth cloth);

    @Query(value = """
    SELECT ci.*
    FROM cloth_image ci
    INNER JOIN (
        SELECT cloth_id, MIN(id) AS min_id
        FROM cloth_image
        WHERE cloth_id IN (:clothIds)
        GROUP BY cloth_id
    ) AS firsts ON ci.id = firsts.min_id
""", nativeQuery = true)
    List<ClothImage> findFirstImagesByClothIds(@Param("clothIds") List<Long> clothIds);

    void deleteAllByCloth(Cloth cloth);
}
