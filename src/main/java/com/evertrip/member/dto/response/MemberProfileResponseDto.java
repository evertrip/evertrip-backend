package com.evertrip.member.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberProfileResponseDto {


    private Long memberId;

    private String nickName;

    private String description;

    private String createdAt;

    private String updatedAt;

    private String profileImage;

    private String email;

    public MemberProfileResponseDto(Long memberId, String nickName, String description, LocalDateTime createdAt, LocalDateTime updatedAt, String profileImage,String email) {
        this.memberId = memberId;
        this.nickName = nickName;
        this.description = description;
        this.createdAt = createdAt.toString().replace("T", " ");
        this.updatedAt = updatedAt.toString().replace("T", " ");
        this.profileImage = profileImage;
        this.email = email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
