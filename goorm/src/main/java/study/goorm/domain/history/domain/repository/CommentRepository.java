package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.goorm.domain.history.domain.entity.Comment;
import study.goorm.domain.history.domain.entity.History;

public interface CommentRepository extends JpaRepository<Comment, Long>{
    long countByHistoryId(Long historyId);
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.history = :history")
    void deleteByHistory(@Param("history") History history);
}
