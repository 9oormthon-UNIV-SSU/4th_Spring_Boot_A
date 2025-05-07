package study.goorm.domain.cloth.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.cloth.domain.entity.ClothImage;

public interface ClothImageRepository extends JpaRepository<ClothImage, Long>{
}
