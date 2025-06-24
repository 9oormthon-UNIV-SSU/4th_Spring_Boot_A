package study.goorm.domain.member.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "member_follow")
public class MemberFollow {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne @JoinColumn(name = "follower_id")
    private Member follower;

    @ManyToOne @JoinColumn(name = "followed_id")
    private Member followed;

}
