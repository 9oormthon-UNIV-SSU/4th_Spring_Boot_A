package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.history.domain.entity.HistoryCloth;

import java.util.List;

public interface HistoryClothRepository extends JpaRepository<HistoryCloth, Long>{

    void deleteAllByCloth(Cloth cloth);

    @Query("SELECT hc FROM HistoryCloth hc JOIN FETCH hc.cloth c WHERE hc.history.id = :historyId")
    List<HistoryCloth> findByHistoryIdWithCloth(@Param("historyId") Long historyId);
}
