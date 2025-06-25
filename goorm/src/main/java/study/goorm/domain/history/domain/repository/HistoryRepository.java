package study.goorm.domain.history.domain.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.dto.HistoryCommentParamDTO;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.history.domain.entity.Comment;

import java.time.LocalDate;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long>{
    // 이렇게도 쓸 수 있구나!
    @Query("SELECT h FROM History h " +
            "WHERE h.member.id = :memberId AND FUNCTION('DATE_FORMAT', h.historyDate, '%Y-%m') = :yearMonth")
    List<History> findHistoriesByMemberIdAndYearMonth(@Param("memberId") Long memberId, @Param("yearMonth") String yearMonth);

    History findById(long historyId);

    @Query(
            "SELECT new study.goorm.domain.history.dto.HistoryCommentParamDTO(" +
                    "  c.id, " +                                            // commentId
                    "  c.content, " +                                       // content
                    "  CASE WHEN c.comment.id IS NULL THEN true ELSE false END, " + // isRoot
                    "  c.comment.id, " +                                     // parentId
                    "  c.member.clokeyId, " +                              // clokeyId
                    "  c.member.nickname, " +                              // nickname
                    "  c.member.profileImageUrl, " +                       // profileImageUrl
                    "  c.createdAt" +                                     // createdAt
                    ") " +
                    "FROM Comment c " +
                    "WHERE c.history.id = :historyId " +
                    "ORDER BY " +
                    "  COALESCE(c.comment.id, c.id) ASC, " +
                    "  c.createdAt ASC"
    )
    List<HistoryCommentParamDTO> findFlatCommentsByHistoryId(
            @Param("historyId") Long historyId,
            Pageable pageable
    );
}
