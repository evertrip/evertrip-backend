package com.evertrip.post.service;

import com.evertrip.api.exception.ApplicationException;
import com.evertrip.api.exception.ErrorCode;
import com.evertrip.api.response.ApiResponse;
import com.evertrip.constant.ConstantPool;
import com.evertrip.file.common.BasicImage;
import com.evertrip.file.common.TableName;
import com.evertrip.file.dto.request.FileRequestDto;
import com.evertrip.file.entity.File;
import com.evertrip.file.entity.FileInfo;
import com.evertrip.file.service.FileService;
import com.evertrip.member.entity.Member;
import com.evertrip.member.repository.MemberRepository;
import com.evertrip.post.dto.request.PostPatchDto;
import com.evertrip.post.dto.request.PostRequestDto;
import com.evertrip.post.dto.response.PostResponseDto;
import com.evertrip.post.dto.response.PostSimpleResponseDto;
import com.evertrip.post.entity.Post;
import com.evertrip.post.entity.PostDetail;
import com.evertrip.post.repository.PostDetailRepository;
import com.evertrip.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {

    private final MemberRepository memberRepository;

    private final PostRepository postRepository;

    private final FileService fileService;


    private final PostDetailRepository postDetailRepository;

    private final RedisForSetService redisForSetService;

    private final CacheManager cacheManager;

    private final PostCacheService postCacheService;

    public ApiResponse<PostResponseDto> getPostDetailV1(Long postId, Long memberId) {

        // 해당 게시글 조회 시 레디스에 게시글 방문자 명단 확인
        if (!redisForSetService.isMember(ConstantPool.CacheName.VIEWERS + ":" + postId, memberId.toString())) {
            // 게시글 방문자 명단에 없을 시 해당 사용자를 명단에 추가해주고 post 엔티티 조회 후 조회수 1 증가 시키기
            redisForSetService.addToset(ConstantPool.CacheName.VIEWERS + ":" + postId, memberId.toString());

            Post post = postRepository.getPostNotDeleteById(postId).orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND));
            post.plusView();
        }

        // PostDetail 조회 해오기
        PostResponseDto postDetail = postRepository.getPostDetail(postId).orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND));
        return ApiResponse.successOf(postDetail);
    }

    public PostResponseDto getPostDetailV2(Long postId, Long memberId) {
        // 레디스에 해당 post가 존재할 시 레디스 정보를 넘겨주고 없을 시 실제 DB 조회 후 레디스에 저장
        PostResponseDto postDetail = postCacheService.getPostDetailUsingCachable(postId);

        // 조회수(제일 최신)는 Redis에서 조회해서 postDetail의 조회수에 넣어줍니다.
        Long views = postCacheService.getViews(postId).longValue();

        // 방문자 리스트에 해당 사용자가 존재하지 않을 시 방문자 리스트에 추가해주고 조회수 1 증가 시켜주기
        if (!redisForSetService.isMember(ConstantPool.CacheName.VIEWERS + ":" + postId, memberId.toString())) {
            // Redis에 방문자 명단 추가
            redisForSetService.addToset(ConstantPool.CacheName.VIEWERS + ":" + postId, memberId.toString());

            // 수동으로 cacheManager를 통해 redis에 조회수 +1 증가 시켜주기
            String viewsCacheKey = postId.toString();
            Cache viewsCache = cacheManager.getCache(ConstantPool.CacheName.VIEWS);
            viewsCache.put(viewsCacheKey, views+1);

            views = views+1;
        }
        postDetail.setView(views);
        return postDetail;
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

        // 레디스에 해당 post 정보 저장해주기
        PostResponseDto postResponseDto = postRepository.getPostDetail(post.getId()).orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND));
        cachePost(postResponseDto);

        return ApiResponse.successOf(new PostSimpleResponseDto(post.getId()));
    }


    public ApiResponse<PostSimpleResponseDto> deletePost(Long memberId, Long postId) {
        Post post = postRepository.getPostNotDeleteById(postId).orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND));

        // 해당 게시글 작성자 본인이 아닐 경우 예외 발생
        if (post.getMember().getId() != memberId) {
            throw new ApplicationException(ErrorCode.NOT_WRITER);
        }

        // TODO: 게시글 관련 엔티티 삭제 처리(좋아요, 태그, 게시글 로그, 댓글, 파일)

        // 레디스에 해당 게시글 관련 정보 삭제
        removeCachePost(postId);

        // 파일 정보 삭제
        fileService.deleteFileList(FileRequestDto.create(TableName.POST, post.getId()));

        // Post, PostDetail 소프트 삭제
        post.deletePost();

        PostDetail postDetail = postDetailRepository.findByPostId(post.getId()).orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND));
        postDetail.deletePostDetail();

        return ApiResponse.successOf(new PostSimpleResponseDto(post.getId()));
    }

    public ApiResponse<PostSimpleResponseDto> updatePost(Long memberId, Long postId, PostPatchDto dto) {
        Post post = postRepository.getPostNotDeleteById(postId).orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND));

        // 해당 게시글 작성자 본인이 아닐 경우 예외 발생
        if (post.getMember().getId() != memberId) {
            throw new ApplicationException(ErrorCode.NOT_WRITER);
        }

        // 게시글 이미지 - fileId 값이 있는 경우
        if (dto.getFileId() != null) {

            // 게시글 이미지 세팅
            File file = fileService.findFile(dto.getFileId());
            fileService.checkFileExtForProfile(file.getFileName());

            // 기본 이미지 아닐 경우 기존 파일 정보 삭제
            if (!post.getProfileImage().equals(BasicImage.BASIC_POST_IMAGE.getPath())) {
                FileRequestDto fileDto = FileRequestDto.create(TableName.POST, post.getId());
                fileService.deleteFileList(fileDto);
            }

            // 공통 : 파일 정보 저장
            fileService.saveFileInfo(new FileInfo(TableName.POST, post.getId(), file));

            // 이미지 세팅
            dto.setProfileImage(file.getPath());

        } else if (!StringUtils.hasText(dto.getProfileImage())) {

            // 이미지를 제거한 상태이기 때문에 기본 이미지로 세팅해주는 작업
            dto.setProfileImage(BasicImage.BASIC_POST_IMAGE.getPath());

            // 기존 파일 정보 제거
            FileRequestDto postFile = FileRequestDto.create(TableName.POST, post.getId());
            fileService.deleteFileList(postFile);
        }

        // Post 수정
        post.updatePost(dto.getTitle(), dto.getProfileImage());

        // PostDetail 수정
        PostDetail postDetail = postDetailRepository.findByPostId(post.getId()).orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND));
        postDetail.updateContent(dto.getContent());

        // Todo: 태그 관련 수정 (태그 분기 처리)

        // 레디스에 해당 post 수정사항 적용
        PostResponseDto postResponseDto = postRepository.getPostDetail(post.getId()).orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND));
        cachePost(postResponseDto);

        return ApiResponse.successOf(new PostSimpleResponseDto(post.getId()));
    }

    private void cachePost(PostResponseDto dto) {
        Cache cache = cacheManager.getCache(ConstantPool.CacheName.POST);
        cache.put(dto.getPostId(), dto);
    }

    private void removeCachePost(Long postId) {
        // POST 관련 삭제
        Cache postCache = cacheManager.getCache(ConstantPool.CacheName.POST);
        postCache.evict(postId);

        // VIEWS 관련 삭제
        removeCacheView(postId);

        // VIEWERS 관련 삭제
        removeCacheViewers(postId);

    }

    private void removeCacheView(Long postId) {
        Cache viewsCache = cacheManager.getCache(ConstantPool.CacheName.VIEWS);
        viewsCache.evict(postId);
    }

    private void removeCacheViewers(Long postId) {
        redisForSetService.deleteSet(ConstantPool.CacheName.VIEWERS+":"+postId);
    }


}
