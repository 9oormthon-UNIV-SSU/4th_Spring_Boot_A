package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.goorm.domain.history.domain.entity.Comment;
import study.goorm.domain.history.domain.entity.History;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>{

    void deleteAllByHistory(History history);

    @Query("SELECT c FROM Comment c JOIN FETCH c.member WHERE c.id = :id")
    Optional<Comment> findByIdWithMember(@Param("id") Long id);
}
