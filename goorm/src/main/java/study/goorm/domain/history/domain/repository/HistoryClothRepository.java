package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryCloth;

public interface HistoryClothRepository extends JpaRepository<HistoryCloth, Long>{
    void deleteAllByCloth(Cloth cloth);
    void deleteByHistory(History history);
}
