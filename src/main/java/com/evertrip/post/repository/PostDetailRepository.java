package com.evertrip.post.repository;

import com.evertrip.post.entity.PostDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostDetailRepository extends JpaRepository<PostDetail, Long> {

    @Query(value="select pd from PostDetail pd where pd.post.id = :postId and pd.deletedYn = false")
    Optional<PostDetail> findByPostId(@Param("postId") Long postId);
}
