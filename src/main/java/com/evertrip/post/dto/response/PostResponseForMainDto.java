package com.evertrip.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

@Getter
@Setter
@AllArgsConstructor
public class PostResponseForMainDto {
    private Long postId;
    private long memberId;
    private String nickName; //  userProfile
    private String title;
    private DateTime createdAt;
    private Long view;
    private String postImage;
    private String content;
    private Long likeCount;
}
