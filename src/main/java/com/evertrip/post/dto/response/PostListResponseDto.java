package com.evertrip.post.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostListResponseDto {

    Long writerId;

    String writerNickname;

    String writerProfileImage;

    Long postId;

    String postProfileImage;

    Long view;

    Long likeCount;

    String title;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    LocalDateTime createdAt;

}
