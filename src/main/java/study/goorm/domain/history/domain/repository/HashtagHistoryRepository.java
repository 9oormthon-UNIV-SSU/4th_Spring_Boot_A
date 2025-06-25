package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.goorm.domain.history.domain.entity.HashtagHistory;
import study.goorm.domain.history.domain.entity.History;

import java.util.List;

public interface HashtagHistoryRepository extends JpaRepository<HashtagHistory, Long> {

    @Query("SELECT hh FROM HashtagHistory hh JOIN FETCH hh.hashtag WHERE hh.history.id = :historyId")
    List<HashtagHistory> findByHistoryId(@Param("historyId") Long historyId);
    void deleteByHistory(History history);
    void deleteAllByHistory(History history);
}
