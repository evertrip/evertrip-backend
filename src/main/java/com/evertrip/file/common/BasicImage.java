package com.evertrip.file.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BasicImage {

    BASIC_USER_IMAGE("https://evertrip-bucket.s3.ap-northeast-2.amazonaws.com/basic_user_image.jpg"),
    BASIC_POST_IMAGE("https://evertrip-bucket.s3.ap-northeast-2.amazonaws.com/basic_post_image.jpg");

    private String path;
}

