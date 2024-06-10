package com.evertrip.post.controller;

import com.evertrip.api.response.ApiResponse;
import com.evertrip.post.dto.response.postlog.*;
import com.evertrip.post.service.PostCacheService;
import com.evertrip.post.service.PostLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/post-logs/{post-id}")
public class PostLogController {

    private final PostLogService postLogService;

    private final PostCacheService postCacheService;

    /**
     * 게시글 총 조회수 조회하기
     */
    @GetMapping("/views")
    public ResponseEntity getTotalViews(@PathVariable("post-id") Long postId) {
        Integer views = postCacheService.getViews(postId);
        return ResponseEntity.ok(ApiResponse.successOf(views));
    }

    /**
     * 게시글 최근 방문자 수 조회
     */
    @GetMapping("/visitors")
    public ResponseEntity getNumberOfVisitors(@PathVariable("post-id") Long postId) {
        List<PostLogVisitorsDto> dtoList = postLogService.getNumberOfVisitors(postId);
        return ResponseEntity.ok(ApiResponse.successOf(dtoList));
    }

    /**
     * 게시글 최근 방문 회원 HISTORY 조회
     */
    @GetMapping("/visitors/history")
    public ResponseEntity getVisitorsHistory(@PathVariable("post-id") Long postId) {
        List<PostLogVisitorsHistoryDto> dtoList = postLogService.getVisitorsHistory(postId);
        return ResponseEntity.ok(ApiResponse.successOf(dtoList));
    }

    /**
     * 게시글 평균 스크롤 깊이 조회
     */
    @GetMapping("/scroll")
    public ResponseEntity getVisitorsScroll(@PathVariable("post-id") Long postId) {
        List<PostLogScrollDto> dtoList = postLogService.getVisitorsScroll(postId);
        return ResponseEntity.ok(ApiResponse.successOf(dtoList));
    }

    /**
     * 게시글 평균 페이지 머문 시간 조회
     */
    @GetMapping("/staying")
    public ResponseEntity getVisitorsStaying(@PathVariable("post-id") Long postId) {
        List<PostLogStayingDto> dtoList = postLogService.getVisitorsStaying(postId);
        return ResponseEntity.ok(ApiResponse.successOf(dtoList));
    }

    /**
     * 댓글 작성, 좋아요 클릭 해제 HISTORY 조회
     */
    @GetMapping("/history")
    public ResponseEntity getHistory(@PathVariable("post-id") Long postId) {
        List<PostLogHistoryDto> dtoList = postLogService.getHistory(postId);
        return ResponseEntity.ok(ApiResponse.successOf(dtoList));
    }


}
