package com.evertrip.member.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberProfileResponseDto {


    private Long memberId;

    private String nickName;

    private String description;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private String profileImage;

    private String email;

    public MemberProfileResponseDto(Long memberId, String nickName, String description, LocalDateTime createdAt, LocalDateTime updatedAt, String profileImage,String email) {
        this.memberId = memberId;
        this.nickName = nickName;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.profileImage = profileImage;
        this.email = email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
