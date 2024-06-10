package com.evertrip.post.dto.response.postlog;

import com.evertrip.constant.ConstantPool;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostLogHistoryDto {

    private Long memberId;

    private String nickName;

    private String profileImage;

    private ConstantPool.EventType eventType;

    private String eventContent;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime eventCreatedAt;

}
