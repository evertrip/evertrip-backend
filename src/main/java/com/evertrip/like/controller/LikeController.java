package com.evertrip.like.controller;

import com.evertrip.api.response.ApiResponse;
import com.evertrip.like.dto.response.LikeMembersResponseDto;
import com.evertrip.like.service.LikeService;
import com.evertrip.member.dto.response.MemberSimpleResponseDto;
import com.evertrip.post.dto.response.PostSimpleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    /*단일 글에 대해서 좋아요 한 사람들의 member_id */
    @GetMapping("/permit/like/members/{postId}")  // URL 경로 변수 수정
    public ResponseEntity<ApiResponse<LikeMembersResponseDto>> getLikeMembers(@PathVariable Long postId){
        ApiResponse<LikeMembersResponseDto> response = ApiResponse.successOf(likeService.getLikeMembers(postId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*단일 글에 대해서 좋아요 한 사람들의 수*/
    @GetMapping("permit/like/count/{postId}")
    public ResponseEntity<ApiResponse<LikeCountResponseDto>> getLikeCount(@PathVariable Long postId){
        ApiResponse<LikeCountResponseDto> response = ApiResponse.successOf(likeService.getLikeCount(postId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
