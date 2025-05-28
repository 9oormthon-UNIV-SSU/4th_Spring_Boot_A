package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.history.domain.entity.HashtagHistory;
import study.goorm.domain.history.domain.entity.History;

import java.util.List;

public interface HashtagHistoryRepository extends JpaRepository<HashtagHistory, Long>{
    List<HashtagHistory> findAllByHistory(History history);

    void deleteAllByHistory(History history);

}
