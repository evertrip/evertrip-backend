package com.evertrip.like.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class LikeMembersResponseDto {
    private Long postId;
    private List<Long> memberId;
}
