package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.history.domain.entity.HistoryImage;

import java.util.List;

public interface HistoryImageRepository extends JpaRepository<HistoryImage, Long> {

    List<HistoryImage> findAllByHistoryIdIn(List<Long> historyIds);
}
