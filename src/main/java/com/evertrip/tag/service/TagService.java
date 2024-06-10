package com.evertrip.tag.service;

import com.evertrip.tag.dto.request.TagNameRequestDto;
import com.evertrip.tag.dto.request.TagRequestDto;
import com.evertrip.tag.dto.response.TagSimpleResponseDto;
import com.evertrip.tag.entity.Tag;
import com.evertrip.tag.repository.PostTagsRepository;
import com.evertrip.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.metrics.StartupStep;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class TagService {

    private final TagRepository tagRepository;
    private final PostTagsRepository postTagsRepository;

    @Cacheable(cacheNames = "tagList")

    //다중 태그 작성
    public List<TagSimpleResponseDto> createTags(List<TagNameRequestDto> tagNames){
        System.out.println(tagNames);

        List<Tag> tags = tagNames.stream()
                .map(name -> name.getTagName())
                .distinct() // 중복 구별
                .filter(name -> !isTagNameContains(name)) //목록 포함 되지 않은 것
                .map( name-> new Tag(0,name))// 객체 생성
                .collect(Collectors.toList());

        List<Tag> createdTags =  tagRepository.saveAll(tags);
        return createdTags.stream().map( i -> i.getTagId())
                .map(TagSimpleResponseDto::new)
                .collect(Collectors.toList());
    }

    //태그 수정
    public TagSimpleResponseDto updateTag(TagRequestDto tag){
        Tag response =  tagRepository.save(new Tag(tag.getTagId(), tag.getTagName()));
        return new TagSimpleResponseDto(response.getTagId());
    }

    //전체 태그
    public List<Tag> getAllTags() {
        return tagRepository.findAllDistinctNames();
    }


    //최근 게시글 태그 15개 들고오기
    public List<Tag> getRecentPostTags(Pageable pageable) {
        return tagRepository.findRecentPostTags(pageable);
    }

    //태그 있는지 확인
    public boolean isTagNameContains(String tagName){
        Tag tag = tagRepository.findByTagName(tagName);
        if(tag == null){
            return false;
        }
        return true;
    }
}
