package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.goorm.domain.history.domain.entity.HistoryImage;

import java.util.List;

public interface HistoryImageRepository extends JpaRepository<HistoryImage, Long>{

    @Query("SELECT hi FROM HistoryImage hi JOIN FETCH hi.history h JOIN FETCH h.member m WHERE m.clokeyId = :clokeyId AND FUNCTION('DATE_FORMAT', h.historyDate, '%Y-%m') = :month ORDER BY h.historyDate ASC, hi.id ASC")
    List<HistoryImage> findMonthlyHistoryImagesWithDetails(@Param("clokeyId") String clokeyId, @Param("month") String month);
}
