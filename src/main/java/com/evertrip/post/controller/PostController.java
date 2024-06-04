package com.evertrip.post.controller;

import com.evertrip.api.exception.ApplicationException;
import com.evertrip.api.exception.ErrorCode;
import com.evertrip.api.response.ApiResponse;
import com.evertrip.constant.ConstantPool;
import com.evertrip.post.dto.request.PostPatchDto;
import com.evertrip.post.dto.request.PostRequestDto;
import com.evertrip.post.dto.request.PostRequestDtoForSearch;
import com.evertrip.post.dto.response.*;
import com.evertrip.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static com.evertrip.constant.ConstantPool.DEFAULT_PAGE_NUM;
import static com.evertrip.constant.ConstantPool.DEFAULT_PAGE_SIZE;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    /**
     * 게시글 단일 상세 조회
     */
    @GetMapping("/{post-id}/v1")
    public ResponseEntity<ApiResponse<PostResponseDto>> getPostDetailV1(@PathVariable("post-id") Long postId,Principal principal) {
        Long memberId = Long.parseLong(principal.getName());
        ApiResponse<PostResponseDto> postDetail = postService.getPostDetailV1(postId,memberId);
        return ResponseEntity.ok(postDetail);
    }

    /**
     * 게시글 단일 상세 조회 (캐싱 적용)
     */
    @GetMapping("/{post-id}/v2")
    public ResponseEntity<ApiResponse<PostResponseDto>> getPostDetailV2(@PathVariable("post-id") Long postId, Principal principal) {
        Long memberId = Long.parseLong(principal.getName());
        PostResponseDto postDetail = postService.getPostDetailV2(postId,memberId);
        return ResponseEntity.ok(ApiResponse.successOf(postDetail));
    }


    @GetMapping("/permit/best30")
    public ResponseEntity<ApiResponse<List<PostResponseForMainDto>>> getPostBest30() {
        Pageable pageable = PageRequest.of(0, ConstantPool.MAIN_POST_SIZE_FOR_SLIDE);
        ApiResponse<List<PostResponseForMainDto>> response = ApiResponse.successOf(postService.getPostBest30(pageable));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/permit/view30")
    public ResponseEntity<ApiResponse<List<PostResponseForMainDto>>> getPostView30() {
        Pageable pageable = PageRequest.of(0, ConstantPool.MAIN_POST_SIZE_FOR_SLIDE);
        ApiResponse<List<PostResponseForMainDto>> response = ApiResponse.successOf(postService.getPostView30(pageable));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/permit/main/searchBar/{page}")
    public ResponseEntity<ApiResponse<Page<PostResponseForSearchDto>>> getPostBySearch(@RequestBody PostRequestDtoForSearch requestDto, @PathVariable long page){
        Pageable pageable = PageRequest.of((int)page, 5);
        ApiResponse<Page<PostResponseForSearchDto>> response = ApiResponse.successOf(postService.getPostBySearch(requestDto, pageable));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 게시글 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PostSimpleResponseDto>> createPost(@Valid @RequestBody PostRequestDto dto, BindingResult bindingResult, Principal principal) {

        // validation 체크
        if (bindingResult.hasErrors()) {
            throw new ApplicationException(ErrorCode.INCORRECT_FORMAT_POST);
        }

        Long memberId = Long.parseLong(principal.getName());
        ApiResponse<PostSimpleResponseDto> response = postService.createPostV2(dto, memberId);
        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{post-id}")
    public ResponseEntity<ApiResponse<PostSimpleResponseDto>> deletePost(@PathVariable("post-id") Long postId, Principal principal) {
        Long memberId = Long.parseLong(principal.getName());
        ApiResponse<PostSimpleResponseDto> response = postService.deletePost(memberId, postId);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    /**
     * 게시글 수정
     */
    @PatchMapping("/{post-id}")
    public ResponseEntity<ApiResponse<PostSimpleResponseDto>> updatePost(@PathVariable("post-id") Long postId,
                                                                         @Valid @RequestBody PostPatchDto postPatchDto,
                                                                         BindingResult bindingResult,
                                                                         Principal principal) {
        // validation 체크
        if (bindingResult.hasErrors()) {
            throw new ApplicationException(ErrorCode.INCORRECT_FORMAT_POST);
        }

        Long memberId = Long.parseLong(principal.getName());
        ApiResponse<PostSimpleResponseDto> response = postService.updatePost(memberId, postId, postPatchDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 내 게시글 목록 조회
     */
    @GetMapping("/my-posts")
    public ResponseEntity getMyPostList(Principal principal, @PageableDefault(size = DEFAULT_PAGE_SIZE, page = DEFAULT_PAGE_NUM
    ,sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long memberId = Long.parseLong(principal.getName());

        // 기본 Sort 설정
        Sort sort = pageable.getSort();
        // Pageable 객체 생성
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<PostListResponseDto> myPostList = postService.getMyPostList(memberId, sortedPageable);
        return new ResponseEntity(ApiResponse.successOf(myPostList), HttpStatus.OK);
    }

    /**
     * 내가 좋아요한 게시글 목록 조회
     */
    @GetMapping("/like-posts")
    public ResponseEntity getLikePostList(Principal principal, @PageableDefault(size = DEFAULT_PAGE_SIZE, page = DEFAULT_PAGE_NUM
            ,sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long memberId = Long.parseLong(principal.getName());

        // 기본 Sort 설정
        Sort sort = pageable.getSort();
        // Pageable 객체 생성
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<PostListResponseDto> likePostList = postService.getLikePostList(memberId, sortedPageable);
        return new ResponseEntity(ApiResponse.successOf(likePostList), HttpStatus.OK);
    }


}
