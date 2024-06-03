package com.evertrip.post.dto.response.postlog;

import com.evertrip.constant.ConstantPool;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostLogVisitorsHistoryDto {

    private Long memberId;

    private String nickName;

    private String profileImage;


    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime eventCreatedAt;
}
