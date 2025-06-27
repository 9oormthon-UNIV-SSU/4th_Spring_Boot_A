package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.MemberLike;
import study.goorm.domain.member.domain.entity.Member;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long>{

    // 이렇게 쿼리문을 써도 괜찮을까요..? -> 직접 쿼리 작성해야함 ㅇㅇ
    @Query("SELECT h FROM History h WHERE h.member.id = :memberId AND FUNCTION('DATE_FORMAT', h.historyDate, '%Y-%m') = :monthStr")
    List<History> findByMemberIdAndMonth(@Param("memberId") Long memberId, @Param("monthStr") String monthStr);

    default List<History> findByMemberIdAndMonth(Long memberId, YearMonth month) {
        return findByMemberIdAndMonth(memberId, month.toString());
    }

    Optional<History> findByMe