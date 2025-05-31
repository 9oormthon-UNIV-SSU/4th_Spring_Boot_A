package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import study.goorm.domain.history.domain.entity.Comment;
import study.goorm.domain.history.domain.entity.History;

public interface CommentRepository extends JpaRepository<Comment, Long>{

    @Modifying
    @Transactional
    void deleteAllByHistory(History history);
}
