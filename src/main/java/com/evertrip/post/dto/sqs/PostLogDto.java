package com.evertrip.post.dto.sqs;

import com.evertrip.constant.ConstantPool;
import com.evertrip.post.LocalDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString
public class PostLogDto {

    private Long postId;

    private ConstantPool.EventType eventType;

    private Long memberId;

    private String eventContent;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime  createdAt;

    public PostLogDto(Long memberId, Long postId, LocalDateTime createdAt, ConstantPool.EventType eventType, String eventContent) {
        this.memberId = memberId;
        this.postId = postId;
        this.eventType = eventType;
        this.eventContent = eventContent;
        this.createdAt = createdAt;
    }
}
