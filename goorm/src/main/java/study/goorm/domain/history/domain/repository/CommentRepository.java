package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.history.domain.entity.Comment;
import study.goorm.domain.history.domain.entity.History;

public interface CommentRepository extends JpaRepository<Comment, Long>{
    void deleteAllByHistory(History history);
}
