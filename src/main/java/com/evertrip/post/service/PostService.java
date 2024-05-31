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
import com.evertrip.file.entity.PostContentFile;
import com.evertrip.file.repository.PostContentFileRepository;
import com.evertrip.file.service.FileService;
import com.evertrip.member.entity.Member;
import com.evertrip.member.entity.MemberProfile;
import com.evertrip.member.repository.MemberProfileRepository;
import com.evertrip.member.repository.MemberRepository;
import com.evertrip.post.dto.request.PostPatchDto;
import com.evertrip.post.dto.request.PostRequestDto;
import com.evertrip.post.dto.request.PostRequestDtoForSearch;
import com.evertrip.post.dto.response.PostResponseDto;
import com.evertrip.post.dto.response.PostResponseForMainDto;
import com.evertrip.post.dto.response.PostResponseForSearchDto;
import com.evertrip.post.dto.response.PostSimpleResponseDto;
import com.evertrip.post.entity.Post;
import com.evertrip.post.entity.PostDetail;
import com.evertrip.post.repository.PostDetailRepository;
import com.evertrip.post.repository.PostRepository;
import com.evertrip.post.specifications.PostSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {

    private final MemberRepository memberRepository;

    private final PostRepository postRepository;

    private final FileService fileService;


    private final PostDetailRepository postDetailRepository;

    private final RedisForCacheService redisForCacheService;

    private final CacheManager cacheManager;
    private final MemberProfileRepository memberProfileRepository;

    private final PostCacheService postCacheService;

    private final PostContentFileRepository postContentFileRepository;

    private final RedissonClient redissonClient;

    public ApiResponse<PostResponseDto> getPostDetailV1(Long postId, Long memberId) {

        // 해당 게시글 조회 시 레디스에 게시글 방문자 명단 확인
        if (!redisForCacheService.isMember(ConstantPool.CacheName.VIEWERS + ":" + postId, memberId.toString())) {
            // 게시글 방문자 명단에 없을 시 해당 사용자를 명단에 추가해주고 post 엔티티 조회 후 조회수 1 증가 시키기
            redisForCacheService.addToset(ConstantPool.CacheName.VIEWERS + ":" + postId, memberId.toString());

            Post post = postRepository.getPostNotDeleteById(postId).orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND));
            post.plusView();
        }

        // PostDetail 조회 해오기
        PostResponseDto postDetail = postRepository.getPostDetail(postId).orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND));
        return ApiResponse.successOf(postDetail);
    }

    public PostResponseDto getPostDetailV2(Long postId, Long memberId) {
        // 레디스에 해당 post가 존재할 시 레디스 정보를 넘겨주고 없을 시 실제 DB 조회 후 레디스에 저장
        PostResponseDto postDetail = postCacheService.getPostDetailUsingCacheable(postId);

        // 조회수(제일 최신)는 Redis에서 조회해서 postDetail의 조회수에 넣어줍니다.
        Long views = postCacheService.getViews(postId).longValue();

        // 방문자 리스트에 해당 사용자가 존재하지 않을 시 방문자 리스트에 추가해주고 조회수 1 증가 시켜주기
        if (!redisForCacheService.isMember(ConstantPool.CacheName.VIEWERS + ":" + postId, memberId.toString())) {
            // Redis에 방문자 명단 추가
            redisForCacheService.addToset(ConstantPool.CacheName.VIEWERS + ":" + postId, memberId.toString());

            // Redisson 락킹 적용
            String lockKey = "lock:viewCount:"+postId;
            RLock lock = redissonClient.getLock(lockKey);

            try {
                // 락을 얻기 위한 재시도(Lock 획득을 시도하는 최대 시간, 락을 획득한 후 점유하는 최대 시간, 시간 단위)
                if (lock.tryLock(5, 2, TimeUnit.SECONDS)) {
                    try {
                        // 수동으로 cacheManager를 통해 redis에 조회수 +1 증가 시켜주기
                        String viewsCacheKey = postId.toString();
                        Cache viewsCache = cacheManager.getCache(ConstantPool.CacheName.VIEWS);
                        Cache.ValueWrapper valueWrapper = viewsCache.get(postId.toString());
                        Long currentViews = ((Integer) valueWrapper.get()).longValue();
                        viewsCache.put(viewsCacheKey, currentViews + 1L);
                        views = currentViews+1L;
                    } finally {
                        lock.unlock();
                    }
                } else {
                    // 락을 얻지 못한 경우 처리
                    log.warn("Could not acquire lock for key: {}", lockKey);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Could not acquire lock for key: {}",lockKey);
                throw new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }

        postDetail.setView(views);
        return postDetail;
    }

    /**
     * 게시글 생성 로직 V1 - JPA saveAll() 사용
     */
    public ApiResponse<PostSimpleResponseDto> createPostV1(PostRequestDto dto, Long memberId) {
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

        // PostContentFile 저장
        if (dto.getContentFileIds()!=null && dto.getContentFileIds().size()!=0) {
            List<Long> contentFileIds = dto.getContentFileIds();
            List<PostContentFile> postContentFileList = new ArrayList<>();
            for (Long id: contentFileIds) {
                PostContentFile postContentFile = new PostContentFile(id, post.getId());
                postContentFileList.add(postContentFile);
            }
            postContentFileRepository.saveAll(postContentFileList);
        }

        // Todo: TagsId 여부에 따른 분기 처리

        // 레디스에 해당 post 정보 저장해주기
        PostResponseDto postResponseDto = postRepository.getPostDetail(post.getId()).orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND));
        cachePost(postResponseDto);



        return ApiResponse.successOf(new PostSimpleResponseDto(post.getId()));
    }


    /**
     * 게시글 생성 로직 V2 - JDBC BATCH INSERT 사용
     */
    public ApiResponse<PostSimpleResponseDto> createPostV2(PostRequestDto dto, Long memberId) {
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

        // PostContentFile 저장
        if (dto.getContentFileIds()!=null && dto.getContentFileIds().size()!=0) {
            List<Long> contentFileIds = dto.getContentFileIds();
            List<PostContentFile> postContentFileList = new ArrayList<>();
            for (Long id: contentFileIds) {
                PostContentFile postContentFile = new PostContentFile(id, post.getId());
                postContentFileList.add(postContentFile);
            }
            postContentFileRepository.batchInsertPostContentFiles(postContentFileList);
        }

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

        // 해당 게시글 관련 게시글_컨텐츠_파일 테이블 정보 삭제하기
        postContentFileRepository.softDeletePostContentFilesByPostId(postId, LocalDateTime.now());

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
        redisForCacheService.deleteSet(ConstantPool.CacheName.VIEWERS+":"+postId);
    }

    @Cacheable(cacheNames = "Best30")
    public List<PostResponseForMainDto> getPostBest30(Pageable pageable) {
        return postRepository.findTop30Posts(pageable);
    }

    @Cacheable(cacheNames = "view30")
    public List<PostResponseForMainDto> getPostView30(Pageable pageable) {
        return postRepository.findView30Posts(pageable);
    }


    public Page<PostResponseForSearchDto> getPostBySearch(PostRequestDtoForSearch requestDto, Pageable pageable) {
        Specification<Post> spec = Specification.where(PostSpecifications.distinct());

        if (requestDto.getSearchContent() != null && !requestDto.getSearchContent().isEmpty()) {
            spec = spec.and(PostSpecifications.titleContains(requestDto.getSearchContent()));
        }
        if (requestDto.getSearchTags() != null && !requestDto.getSearchTags().isEmpty()) {
            // Stream을 사용하여 각 태그에 대해 스펙을 생성하고 최종적으로 모두 결합
            Specification<Post> tagsSpec = requestDto.getSearchTags().stream()
                    .map(PostSpecifications::tagNameContains)
                    .reduce(Specification::or)
                    .orElse(Specification.where(null));  // 태그가 없는 경우 기본 스펙으로 fallback

            spec = spec.and(tagsSpec);
        }
        Page<Post> postPage = postRepository.findAll(spec, pageable);
        return postPage.map(this::convertToDto);
    }

    private PostResponseForSearchDto convertToDto(Post post) {
        MemberProfile memberProfile = memberProfileRepository.findByMemberId(post.getMember().getId(), post.getMember().isDeletedYn()).orElseThrow();
        PostDetail postDetail = postDetailRepository.findByPostId(post.getId()).orElseThrow();
        PostResponseForSearchDto response =  PostResponseForSearchDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .memberProfileId(memberProfile.getId())
                .memberNickname(memberProfile.getNickName())
                .memberProfileImage(memberProfile.getProfileImage())
                .view(post.getView())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .content(postDetail.getContent())
                .build();
        return response;
    }

}
