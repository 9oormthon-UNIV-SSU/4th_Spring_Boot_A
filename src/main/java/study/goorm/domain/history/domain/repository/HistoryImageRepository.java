package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryImage;

import java.util.List;

public interface HistoryImageRepository extends JpaRepository<HistoryImage, Long> {

    // History
    List<HistoryImage> findAllByHistoryIdIn(List<Long> historyIds);
    List<HistoryImage> findByHistoryId(Long historyId);
    List<HistoryImage> findAllByHistory(History history);
    void deleteAllByHistory(History history);

}
