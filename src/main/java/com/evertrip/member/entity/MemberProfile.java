package com.evertrip.member.entity;

import com.evertrip.common.entity.BaseEntity;
import com.evertrip.file.common.BasicImage;
import com.evertrip.member.dto.request.MemberProfilePatchDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    public MemberProfile(Member member, String name) {
        this.member = member;
        this.nickName = name;
        this.description = "기본 자기 소개글을 작성해주시기 바랍니다.";
        this.deletedYn = false;
        this.profileImage = BasicImage.BASIC_USER_IMAGE.getPath();
    }

    public void changProfile(MemberProfilePatchDto dto) {
        this.profileImage = dto.getProfileImage();
        this.description = dto.getDescription();
        this.nickName = dto.getNickName();
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deletedYn = true;
    }
}
