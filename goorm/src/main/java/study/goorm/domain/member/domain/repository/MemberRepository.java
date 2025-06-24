package study.goorm.domain.member.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.goorm.domain.member.domain.dto.LikedMemberDTO;
import study.goorm.domain.member.domain.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>{
    Optional<Member> findByClokeyId(String clokeyId);

    Member findMemberById(Long memberId);

    @Query(
            "SELECT new study.goorm.domain.member.domain.dto.LikedMemberDTO(" +
                    "m.id, m.clokeyId, m.profileImageUrl, m.nickname, " +
                    "CASE WHEN EXISTS(" +
                    "SELECT 1 FROM MemberFollow f " +
                    "WHERE f.follower.id = :loginMemberId AND f.followed.id = m.id" +
                    ") THEN true ELSE false END, " +
                    "CASE WHEN m.id = :loginMemberId THEN true ELSE false END" +
                    ") " +
                    "FROM MemberLike ml JOIN ml.member m " +
                    "WHERE ml.history.id = :historyId"
    )
    List<LikedMemberDTO> findLikedMembersWithFollowInfo(
            @Param("historyId")     Long historyId,
            @Param("loginMemberId") Long loginMemberId
    );
}
