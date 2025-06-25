package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.MemberLike;
import study.goorm.domain.member.domain.entity.Member;

public interface MemberLikeRepository extends JpaRepository<MemberLike, Long> {

    void deleteByMemberIdAndHistoryId(Long memberId, Long historyId);
}
