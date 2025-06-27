package study.goorm.domain.member.domain.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.member.domain.exception.MemberException;
import study.goorm.domain.member.domain.repository.MemberRepository;
import study.goorm.global.error.code.status.ErrorStatus;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    @Override
    public Member getCurrentMember() {
        Long fakeLoginMemberid = 1L;
        return memberRepository.findById(fakeLoginMemberid)
                .orElseThrow(() -> new MemberException(ErrorStatus.NO_SUCH_MEMBER));
    }

    @Override
    public Optional<Member> findByClokeyId(String clokeyId) {
        return memberRepository.findByClokeyId(clokeyId);
    }
}
