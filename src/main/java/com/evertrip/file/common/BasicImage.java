package com.evertrip.file.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BasicImage {

    BASIC_USER_IMAGE("https://evertrip-bucket.s3.ap-northeast-2.amazonaws.com/basic_user_image.jpg"),
    BASIC_DOG_IMAGE("https://evertrip-bucket.s3.ap-northeast-2.amazonaws.com/basic_dog_image.jpg"),
    BASIC_JOB_POST_IMAGE("https://evertrip-bucket.s3.ap-northeast-2.amazonaws.com/basic_board_image.jpg");

    private String path;
}

