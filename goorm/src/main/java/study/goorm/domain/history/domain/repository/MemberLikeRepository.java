package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.MemberLike;

import java.util.List;

public interface MemberLikeRepository extends JpaRepository<MemberLike, Long>{

    void deleteAllByHistory(History history);

    boolean existsByHistoryIdAndMemberId(Long historyId, Long memberId);

    // ✅ @EntityGraph 어노테이션으로 'member' 필드를 함께 조회하도록 지정 N+1 문제 해결
    @EntityGraph(attributePaths = {"member"})
    List<MemberLike> findAllByHistory(History history);
}