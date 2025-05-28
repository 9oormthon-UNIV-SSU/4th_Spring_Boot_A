package study.goorm.domain.member.domain.application;

import study.goorm.domain.member.domain.entity.Member;

import java.util.Optional;

public interface MemberService {
    Member getCurrentMember();

    Optional<Member> findByClokeyId(String clokeyId);

}
