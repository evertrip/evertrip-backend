package com.evertrip.post.service;

import com.evertrip.api.exception.ApplicationException;
import com.evertrip.api.exception.ErrorCode;
import com.evertrip.constant.ConstantPool;
import com.evertrip.post.dto.response.PostResponseDto;
import com.evertrip.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCacheService {

    private final PostRepository postRepository;

    @Cacheable(value = ConstantPool.CacheName.POST, key = "#postId")
    public PostResponseDto getPostDetailUsingCachable(Long postId) {
        PostResponseDto postDetail = postRepository.getPostDetail(postId).orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND));
        return postDetail;
    }

    @Cacheable(value = ConstantPool.CacheName.VIEWS, key = "#postId")
    public Integer getViews(Long postId) {
        Long views = postRepository.getViews(postId).orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND));
        return views.intValue();
    }
}
