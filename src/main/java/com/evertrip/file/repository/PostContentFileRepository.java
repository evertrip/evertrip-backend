package com.evertrip.file.repository;

import com.evertrip.file.entity.PostContentFile;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface PostContentFileRepository extends JpaRepository<PostContentFile, Long>, PostContentFileCustom {

    @Modifying
    @Query("delete from PostContentFile pcf where pcf.id = :postContentFileId")
    void hardDeletePostContentFile(@Param("postContentFileId") Long postContentFileId);

    @Modifying
    @Query("update PostContentFile pcf set pcf.deletedAt = :now where pcf.postId = :postId")
    void softDeletePostContentFilesByPostId(@Param("postId") Long postId, @Param("now")LocalDateTime now);
}
