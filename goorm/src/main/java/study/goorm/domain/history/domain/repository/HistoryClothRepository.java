package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.history.domain.entity.HistoryCloth;
import study.goorm.domain.history.domain.entity.History;
import java.util.List;


public interface HistoryClothRepository extends JpaRepository<HistoryCloth, Long>{
    void deleteAllByCloth(Cloth cloth);

    List<HistoryCloth> findAllByHistory(History history);

}
