package study.goorm.domain.history.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.goorm.domain.history.domain.entity.MemberLike;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.member.domain.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberLikeRepositor