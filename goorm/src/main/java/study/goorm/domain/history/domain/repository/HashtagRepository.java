package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.history.domain.entity.Hashtag;

import java.util.List;

public interface HashtagRepository extends JpaRepository<Hashtag, Long>{
    List<Hashtag> findAllByNameIn(List<String> attr0);
}
