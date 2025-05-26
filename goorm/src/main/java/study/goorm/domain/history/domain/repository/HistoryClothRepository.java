package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryCloth;

import java.util.List;

public interface HistoryClothRepository extends JpaRepository<HistoryCloth, Long>{

    void deleteAllByCloth(Cloth cloth);

    void deleteAllByHistory(History history);

    List<HistoryCloth> findAllByHistory(History history);
}
