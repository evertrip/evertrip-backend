package com.evertrip.post.repository;

import com.evertrip.post.entity.PostLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLogRepository extends JpaRepository<PostLog, Long> {
}
