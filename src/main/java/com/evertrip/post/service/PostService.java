package com.evertrip.post.service;

import com.evertrip.api.exception.ApplicationException;
import com.evertrip.api.exception.ErrorCode;
import com.evertrip.api.response.ApiResponse;
import com.evertrip.file.common.TableName;
import com.evertrip.file.dto.request.FileRequestDto;
import com.evertrip.file.entity.File;
import com.evertrip.file.entity.FileInfo;
import com.evertrip.file.service.FileService;
import com.evertrip.member.entity.Member;
import com.evertrip.member.repository.MemberRepository;
import com.evertrip.post.dto.request.PostRequestDto;
import com.evertrip.post.dto.response.PostResponseDto;
import com.evertrip.post.dto.response.PostSimpleResponseDto;
import com.evertrip.post.entity.Post;
import com.evertrip.post.entity.PostDetail;
import com.evertrip.post.repository.PostDetailRepository;
import com.evertrip.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final MemberRepository memberRepository;

    private final PostRepository postRepository;

    private final FileService fileService;


    private final PostDetailRepository postDetailRepository;

    @Transactional(readOnly = true)
    public ApiResponse<PostResponseDto> getPostDetail(Long postId) {
        PostResponseDto postDetail = postRepository.getPostDetail(postId).orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND));
        return ApiResponse.successOf(postDetail);
    }

    public ApiResponse<PostSimpleResponseDto> createPost(PostRequestDto dto, Long memberId) {
        Member member = memberRepository.findByIdNotDeleted(memberId).orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        Post post;

        // File Id 여부에 따른 분기 처리
        if (dto.getFileId()==null) {
            post = new Post(member, dto.getTitle());
            postRepository.save(post);
        } else {
            File file = fileService.findFile(dto.getFileId());
            post = new Post(member, dto.getTitle(), file.getPath());
            postRepository.save(post);
            fileService.saveFileInfo(new FileInfo(TableName.POST, post.getId(), file));
        }

        PostDetail postDetail = new PostDetail(post, dto.getContent());
        postDetailRepository.save(postDetail);


        // Todo: TagsId 여부에 따른 분기 처리


        return ApiResponse.successOf(new PostSimpleResponseDto(post.getId()));
    }


    public ApiResponse<PostSimpleResponseDto> deletePost(Long memberId, Long postId) {
        Post post = postRepository.getPostNotDeleteById(postId).orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND));

        // 해당 게시글 작성자 본인이 아닐 경우 예외 발생
        if (post.getMember().getId() != memberId) {
            throw new ApplicationException(ErrorCode.NOT_WRITER);
        }

        // TODO: 게시글 관련 엔티티 삭제 처리(좋아요, 태그, 게시글 로그, 댓글, 파일)

        // 파일 정보 삭제
        fileService.deleteFileList(FileRequestDto.create(TableName.POST, post.getId()));

        // Post, PostDetail 소프트 삭제
        post.deletePost();

        PostDetail postDetail = postDetailRepository.findByPostId(post.getId()).orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND));
        postDetail.deletePostDetail();

        return ApiResponse.successOf(new PostSimpleResponseDto(post.getId()));
    }


}
