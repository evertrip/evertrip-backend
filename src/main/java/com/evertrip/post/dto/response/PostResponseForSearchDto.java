package com.evertrip.post.dto.response;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PostResponseForSearchDto {
    private Long postId;
    private String title;
    private Long memberProfileId;
    private String memberNickname;
    private String memberProfileImage;
    private Long view;
    private Long likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String content;
}
