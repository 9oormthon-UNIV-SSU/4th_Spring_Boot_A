package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.member.domain.entity.Member;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long> {

    @Query("SELECT h FROM History h " +
            "WHERE h.member.id = :memberId AND FUNCTION('DATE_FORMAT', h.historyDate, '%Y-%m') = :yearMonth")
    List<History> findHistoriesByMemberIdAndYearMonth(@Param("memberId") Long memberId, @Param("yearMonth") String yearMonth);

}
