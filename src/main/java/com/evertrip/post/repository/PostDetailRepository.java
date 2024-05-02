package com.evertrip.post.repository;

import com.evertrip.post.entity.PostDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostDetailRepository extends JpaRepository<PostDetail, Long> {
}
