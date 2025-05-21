package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.cloth.domain.entity.ClothImage;

import java.util.List;

public interface ClothImageRepository extends JpaRepository<
        ClothImage,Long> {

    List<ClothImage> findAllByCloth(Cloth cloth);

}
