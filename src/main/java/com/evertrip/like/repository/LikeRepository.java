package com.evertrip.like.repository;

import com.evertrip.like.controller.LikeCountResponseDto;
import com.evertrip.like.dto.response.LikeMembersResponseDto;
import com.evertrip.like.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeRepository extends JpaRepository<PostLike, Long> {

    @Query("SELECT l.memberId FROM PostLike  l WHERE l.postId = :postId group by l.memberId")
    List<Long> findLikeMembersByPostId(@Param("postId") Long postId);

    @Query("SELECT COUNT(DISTINCT pl.memberId) as count FROM PostLike pl WHERE pl.postId = :postId")
    Long findLikeCountByPostId(Long postId);
}



