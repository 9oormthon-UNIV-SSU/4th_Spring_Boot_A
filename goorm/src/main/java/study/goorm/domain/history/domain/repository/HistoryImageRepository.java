package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.cloth.domain.entity.ClothImage;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryImage;

import java.util.List;

public interface HistoryImageRepository extends JpaRepository<HistoryImage, Long>{

    List<HistoryImage> findAllByHistory(History history);

    @Query(value = """
        SELECT hi.*
        FROM history_image hi
        JOIN (
            SELECT history_id, MIN(created_at) AS min_created_at
            FROM history_image
            WHERE history_id IN :historyIds
            GROUP BY history_id
        ) first_img
        ON hi.history_id = first_img.history_id
        AND hi.created_at = first_img.min_created_at
        """, nativeQuery = true)
    List<HistoryImage> findFirstImagesByHistoryIds(@Param("historyIds") List<Long> historyIds);

    void deleteAllByHistory(History history);
}
