package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.history.domain.entity.History;

import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long>{
    Optional<History> findByClokeyId(String clokeyId);
}