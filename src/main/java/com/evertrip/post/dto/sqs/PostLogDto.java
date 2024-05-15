package com.evertrip.post.dto.sqs;

import com.evertrip.constant.ConstantPool;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString
public class PostLogDto {

    private Long postId;

    private ConstantPool.EventType eventType;

    private Long memberId;

    private String token;

    private String eventContent;

    private String createdAt;

}
