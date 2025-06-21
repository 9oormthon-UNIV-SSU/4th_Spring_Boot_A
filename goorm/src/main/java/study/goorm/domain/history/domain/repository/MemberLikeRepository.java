package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.MemberLike;

public interface MemberLikeRepository extends JpaRepository<MemberLike, Long>{
    @Modifying
    @Query("DELETE FROM MemberLike l WHERE l.history = :history")
    void deleteByHistory(@Param("history") History history);
}
