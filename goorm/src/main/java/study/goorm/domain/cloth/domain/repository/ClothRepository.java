package study.goorm.domain.cloth.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // 특정 ID 목록에 해당하는 옷들을 조회
    List<Cloth> findAllByIdIn(List<Long> ids);

    // 옷 착용 횟수 증가 (생성 및 수정 시 사용)
    @Modifying
    @Query("UPDATE Cloth c SET c.wearNum = c.wearNum + 1 WHERE c.id = :clothId")
    void incrementWearNum(@Param("clothId") Long clothId);
}
