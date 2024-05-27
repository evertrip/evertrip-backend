package com.evertrip.post.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.AutoConfiguration;

@Getter
@Setter
@AutoConfiguration
public class PostRequestDtoForSearch {
    private String searchContent;
    private String searchTags;
}
