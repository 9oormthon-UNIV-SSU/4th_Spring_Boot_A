package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.MemberLike;

public interface MemberLikeRepository extends JpaRepository<MemberLike, Long>{

    void deleteAllByHistory(History history);
}
