package com.evertrip.tag.repository;

import com.evertrip.post.entity.Post;
import com.evertrip.tag.entity.PostTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PostTagsRepository  extends JpaRepository<PostTags, Long>, JpaSpecificationExecutor<PostTags> {
}
