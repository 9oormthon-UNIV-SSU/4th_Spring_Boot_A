package study.goorm.domain.cloth.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.cloth.domain.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{
}
