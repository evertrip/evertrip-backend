package com.evertrip.tag.controller;

import com.evertrip.api.response.ApiResponse;
import com.evertrip.tag.dto.request.TagNameRequestDto;
import com.evertrip.tag.dto.request.TagRequestDto;
import com.evertrip.tag.dto.response.TagSimpleResponseDto;
import com.evertrip.tag.entity.Tag;
import com.evertrip.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final TagService tagService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Tag>>> getAllTags(){
        ApiResponse<List<Tag>> response = ApiResponse.successOf(tagService.getAllTags());
        return ResponseEntity.ok(response);
    }

    //최근 게시글 태그 15개 들고오기
    @GetMapping("/recent/{count}")
    public ResponseEntity<ApiResponse<List<Tag>>> getRecentPostTags(@PathVariable int count){
        Pageable pageable = PageRequest.of(0, count);
        ApiResponse<List<Tag>> response = ApiResponse.successOf(tagService.getRecentPostTags(pageable));
        return ResponseEntity.ok(response);
    }

    //다중 태그 작성
    @PostMapping("")
    public ResponseEntity<ApiResponse<List<TagSimpleResponseDto>>> createTags(@RequestBody List<TagNameRequestDto> tagNames){
        ApiResponse<List<TagSimpleResponseDto>> response = ApiResponse.successOf(tagService.createTags(tagNames));
        return ResponseEntity.ok(response);
    }

    @PutMapping("")
    public ResponseEntity<ApiResponse<TagSimpleResponseDto>> updateTag(@RequestBody TagRequestDto tag){
        ApiResponse<TagSimpleResponseDto> response = ApiResponse.successOf(tagService.updateTag(tag));
        return ResponseEntity.ok(response);
    }

}
