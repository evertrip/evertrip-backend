package com.evertrip.post.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Builder
public class PostResponseDto {

    private Long postId;

    private String title;

    private Long memberProfileId;

    private String memberNickname;

    private String memberProfileImage;

    private Long view;

    private Long likeCount;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private String content;

    public PostResponseDto(Long postId, String title, Long memberProfileId, String memberNickname, String memberProfileImage, Long view, Long likeCount, LocalDateTime createdAt, LocalDateTime updatedAt, String content) {
        this.postId = postId;
        this.title = title;
        this.memberProfileId = memberProfileId;
        this.memberNickname = memberNickname;
        this.memberProfileImage = memberProfileImage;
        this.view = view;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.content = content;
    }

    public void setView(Long view) {
        this.view = view;
    }
}
