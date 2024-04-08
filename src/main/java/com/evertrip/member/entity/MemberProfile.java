package com.evertrip.member.entity;

import com.evertrip.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProfile extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_profile_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name="nickname")
    private String nickName;

    @Column(name="description")
    private String description;

    @Column(name="deleted_yn")
    private boolean deletedYn;

    @Column(name="profile_image")
    private String profileImage;
}
