package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.goorm.domain.history.domain.entity.History;

import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long>{

    // History와 Member만 FETCH JOIN
    @Query("SELECT h FROM History h JOIN FETCH h.member m WHERE h.id = :historyId")
    Optional<History> findHistoryAndMemberById(@Param("historyId") Long historyId);

    // 댓글 수 조회
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.history.id = :historyId")
    Long countCommentsByHistoryId(@Param("historyId") Long historyId);

    // 좋아요 수 조회
    @Query("SELECT COUNT(ml) FROM MemberLike ml WHERE ml.history.id = :historyId")
    Long countLikesByHistoryId(@Param("historyId") Long historyId);

    // 특정 멤버가 해당 기록에 좋아요를 눌렀는지 확인
    @Query("SELECT CASE WHEN COUNT(ml) > 0 THEN TRUE ELSE FALSE END FROM MemberLike ml WHERE ml.history.id = :historyId AND ml.member.id = :memberId")
    boolean existsMemberLikeByHistoryIdAndMemberId(@Param("historyId") Long historyId, @Param("memberId") Long memberId);
}
