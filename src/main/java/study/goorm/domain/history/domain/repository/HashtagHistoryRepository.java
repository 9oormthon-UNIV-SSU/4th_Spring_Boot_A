package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.history.domain.entity.HashtagHistory;

import java.util.List;

public interface HashtagHistoryRepository extends JpaRepository<HashtagHistory, Long> {

    List<HashtagHistory> findByHistoryId(Long historyId);
}
