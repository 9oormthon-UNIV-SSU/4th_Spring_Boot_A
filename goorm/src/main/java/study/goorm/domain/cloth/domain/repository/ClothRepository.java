package study.goorm.domain.cloth.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.cloth.domain.entity.Cloth;

public interface ClothRepository extends JpaRepository<Cloth, Long> {
}
