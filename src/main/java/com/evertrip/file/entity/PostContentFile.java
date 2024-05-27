package com.evertrip.file.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class PostContentFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="post_content_file_id")
    private Long id;

    @Column(name="file_id")
    private Long fileId;

    @Column(name="post_id")
    private Long postId;

    @Column(name="deleted_at")
    private LocalDateTime deletedAt;

    public PostContentFile(Long fileId, Long postId) {
        this.fileId = fileId;
        this.postId = postId;
        this.deletedAt = null;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
