package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.history.domain.entity.HashtagHistory;
import study.goorm.domain.history.domain.entity.History;

import java.util.List;

public interface HashtagHistoryRepository extends JpaRepository<HashtagHistory, Long> {

    // History
    List<HashtagHistory> findByHistoryId(Long historyId);
    void deleteByHistory(History history);
    void deleteAllByHistory(History history);
}
