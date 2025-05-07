package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.history.domain.entity.HistoryImage;

public interface HistoryImageRepository extends JpaRepository<HistoryImage, Long> {
}
