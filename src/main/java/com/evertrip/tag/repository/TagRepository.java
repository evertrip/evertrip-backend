package com.evertrip.tag.repository;

import com.evertrip.post.entity.Post;
import com.evertrip.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<Tag, Long>  ,  JpaSpecificationExecutor<Tag> {

}
