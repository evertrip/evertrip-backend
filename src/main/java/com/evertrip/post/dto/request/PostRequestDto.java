package com.evertrip.post.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {

    @Size(min = 20)
    private String title;

    @Size(min = 20)
    private String content;

    private List<Long> tagsId;

    private Long fileId;

}
