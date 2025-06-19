package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.MemberLike;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long>{

    void delete(History history);

    @Query("SELECT h FROM History h WHERE h.member.id = :memberId AND FUNCTION('DATE_FORMAT', h.createdAt, '%Y-%m') = :month")
    List<History> findAllByMemberIdAndMonth(@Param("memberId") Long memberId, @Param("month") String month);
}