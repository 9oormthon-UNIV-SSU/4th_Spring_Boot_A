package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.history.domain.entity.HashtagHistory;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryCloth;

import java.util.List;
import java.util.Optional;

public interface HistoryClothRepository extends JpaRepository<HistoryCloth, Long>{
    // Cloth
    void deleteAllByCloth(Cloth cloth);

    // History
    void deleteByHistory(History history);
    void deleteAllByHistory(History history);
}
