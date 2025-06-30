package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryCloth;

import java.util.List;

public interface HistoryClothRepository extends JpaRepository<HistoryCloth, Long>{

    void deleteAllByCloth(Cloth cloth);

    void deleteAllByHistory(History history);

    // @EntityGraph 어노테이션으로 'cloth' 필드를 함께 조회하도록 지정 N+1 문제 해결
    @EntityGraph(attributePaths = {"cloth"})
    List<HistoryCloth> findAllByHistory(History history);
}
