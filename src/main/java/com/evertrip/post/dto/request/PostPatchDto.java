package com.evertrip.post.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostPatchDto {

    @Size(min = 20)
    private String title;

    @Size(min = 20)
    private String content;

    private List<Long> tagsId;

    private Long fileId;

    @Setter
    private String profileImage;
}
