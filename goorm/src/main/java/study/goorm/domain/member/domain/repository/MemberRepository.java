package study.goorm.domain.member.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.member.domain.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
