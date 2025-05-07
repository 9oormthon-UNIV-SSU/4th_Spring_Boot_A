package study.goorm.domain.member.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.member.domain.entity.Term;

public interface TermRepository extends JpaRepository<Term, Long> {
}
