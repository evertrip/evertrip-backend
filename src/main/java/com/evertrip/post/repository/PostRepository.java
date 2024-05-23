package com.evertrip.post.repository;

import com.evertrip.post.dto.response.PostResponseDto;
import com.evertrip.post.entity.Post;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value = "select new com.evertrip.post.dto.response.PostResponseDto(p.id, p.title, mp.id, mp.nickName, mp.profileImage, p.view, p.likeCount,p.createdAt, p.updatedAt, pd.content ) from Post p " +
            "join PostDetail pd on pd.post.id = p.id and pd.deletedYn = false " +
            "join Member m on m.id = p.member.id and m.deletedYn = false " +
            "join MemberProfile mp on mp.member.id= m.id and mp.deletedYn = false " +
            "where p.id = :postId and p.deletedYn = false")
    Optional<PostResponseDto> getPostDetail(@Param("postId") Long postId);

    @Query(value = "select p from Post p where p.id = :postId and p.deletedYn = false")
    Optional<Post> getPostNotDeleteById(@Param("postId") Long postId);

    @Query(value = "select p.view from Post p where p.id = :postId and p.deletedYn = false")
    Optional<Long> getViews(@Param("postId") Long postId);


}
