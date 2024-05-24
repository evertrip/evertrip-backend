package com.evertrip.like.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LikeCountResponseDto {
    private Long postId;
    private Long count;
}
