package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryImage;

import java.util.List;
import java.util.Optional;

public interface HistoryImageRepository extends JpaRepository<HistoryImage, Long>{
    Optional<HistoryImage> findFirstByHistoryIdOrderByCreatedAtAsc(Long historyId);

    List<HistoryImage> findAllByHistory(History history);

    void deleteAllByHistory(History history);

}
