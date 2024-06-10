package com.evertrip.tag.repository;

import com.evertrip.post.entity.Post;
import com.evertrip.tag.entity.PostTags;
import com.evertrip.tag.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostTagsRepository  extends JpaRepository<PostTags, Long>, JpaSpecificationExecutor<PostTags> {



}
