package study.goorm.domain.cloth.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.member.domain.entity.Member;

import java.util.List;


public interface ClothRepository extends JpaRepository<Cloth, Long> {

    // 1. wearNum 오름차순
    Page<Cloth> findByMemberOrderByWearNumAsc(Member member, Pageable pageable);

    // 2. wearNum 내림차순
    Page<Cloth> findByMemberOrderByWearNumDesc(Member member, Pageable pageable);

    // 3. createdAt 오름차순
    Page<Cloth> findByMemberOrderByCreatedAtAsc(Member member, Pageable pageable);

    // 4. createdAt 내림차순
    Page<Cloth> findByMemberOrderByCreatedAtDesc(Member member, Pageable pageable);

    List<Cloth> findByMemberId(Long memberId);
}
