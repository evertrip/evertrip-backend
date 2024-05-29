package com.evertrip.post.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.AutoConfiguration;

import java.util.List;

@Getter
@Setter
@AutoConfiguration
public class PostRequestDtoForSearch {
    private String searchContent;
    private List<String> searchTags;
}
