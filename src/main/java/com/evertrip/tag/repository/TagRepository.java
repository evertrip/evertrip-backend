package com.evertrip.tag.repository;

import com.evertrip.post.entity.Post;
import com.evertrip.tag.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long>  ,  JpaSpecificationExecutor<Tag> {
    @Query("SELECT t FROM Tag t WHERE t.tagId IN (SELECT MIN(t2.tagId) FROM Tag t2 GROUP BY t2.tagName)")
    List<Tag> findAllDistinctNames();

    @Query("select pt.tag from PostTags pt order by pt.post.createdAt ASC")
    List<Tag> findRecentPostTags(Pageable pageable);

    @Query("select t from Tag t where t.tagName = :tagName")
    Tag findByTagName(String tagName);
}
