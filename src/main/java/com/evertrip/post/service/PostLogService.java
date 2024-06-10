package com.evertrip.post.service;

import com.evertrip.post.dto.response.postlog.*;
import com.evertrip.post.repository.PostLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostLogService {

    private final PostLogRepository postLogRepository;

    public List<PostLogVisitorsDto> getNumberOfVisitors(Long postId) {
        List<PostLogVisitorsDto> dtoList = postLogRepository.getNumberOfVisitors(postId);
        return dtoList;
    }

    public List<PostLogVisitorsHistoryDto> getVisitorsHistory(Long postId) {
        List<PostLogVisitorsHistoryDto> dtoList = postLogRepository.getVisitorsHistory(postId);
        return dtoList;
    }

    public List<PostLogScrollDto> getVisitorsScroll(Long postId) {
        List<PostLogScrollDto> dtoList = postLogRepository.getVisitorsScroll(postId);
        return dtoList;
    }

    public List<PostLogStayingDto> getVisitorsStaying(Long postId) {
        List<PostLogStayingDto> dtoList = postLogRepository.getVisitorsStaying(postId);
        return dtoList;
    }

    public List<PostLogHistoryDto> getHistory(Long postId) {
        List<PostLogHistoryDto> dtoList = postLogRepository.getHistory(postId);
        return dtoList;
    }

}
