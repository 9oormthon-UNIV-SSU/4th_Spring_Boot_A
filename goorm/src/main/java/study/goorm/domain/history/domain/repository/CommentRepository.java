package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import study.goorm.domain.history.domain.entity.Comment;
import study.goorm.domain.history.domain.entity.History;

public interface CommentRepository extends JpaRepository<Comment, Long>{
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.history.id = :historyId")
    Long countByHistoryId(@Param("historyId") Long historyId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.history = :history")
    void deleteByHistory(@Param("history") History history);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.history.id = :historyId AND c.comment IS NULL AND c.banned = false")
    int countActiveRootComments(@Param("historyId") Long historyId);

    // 대댓 삭제
    @Transactional
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.comment.id = :commentId")
    void deleteChildrenComment(@Param("commentId") Long commentId);

}
