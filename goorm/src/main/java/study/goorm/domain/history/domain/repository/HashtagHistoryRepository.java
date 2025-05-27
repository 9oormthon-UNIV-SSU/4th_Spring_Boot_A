package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.history.domain.entity.HashtagHistory;
import study.goorm.domain.history.domain.entity.History;

public interface HashtagHistoryRepository extends JpaRepository<HashtagHistory, Long>{
    void deleteByHistory(History history);
}
