package com.evertrip.like.service;

import com.evertrip.api.response.ApiResponse;
import com.evertrip.like.controller.LikeCountResponseDto;
import com.evertrip.like.dto.response.LikeMembersResponseDto;
import com.evertrip.like.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;


    public LikeMembersResponseDto getLikeMembers(Long postId) {
        List<Long> response = likeRepository.findLikeMembersByPostId(postId);
        return new LikeMembersResponseDto(postId, response);
    }

    public LikeCountResponseDto getLikeCount(Long postId) {
        Long response = likeRepository.findLikeCountByPostId(postId);
        return new LikeCountResponseDto(postId, response);
    }
}
