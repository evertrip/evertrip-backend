package com.evertrip.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostDetail {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="post_detail_id")
    private Long id;

    @Column(name="content")
    private String content;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id")
    private Post post;

    @Column(name="deleted_yn")
    private Boolean deletedYn;

    public PostDetail(Post post, String content) {
        this.post = post;
        this.content = content;
        this.deletedYn = false;
    }

    public void deletePostDetail() {
        this.deletedYn = true;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
