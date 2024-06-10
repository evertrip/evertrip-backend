package com.evertrip.post.repository;

import com.evertrip.post.dto.response.postlog.*;

import java.util.List;

public interface PostLogRepositoryCustom {

    List<PostLogVisitorsDto> getNumberOfVisitors(Long postId);

    List<PostLogVisitorsHistoryDto> getVisitorsHistory(Long postId);

    List<PostLogScrollDto> getVisitorsScroll(Long postId);

    List<PostLogStayingDto> getVisitorsStaying(Long postId);

    List<PostLogHistoryDto> getHistory(Long postId);

}
