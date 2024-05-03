package com.evertrip.post.entity;

import com.evertrip.common.entity.BaseEntity;
import com.evertrip.file.common.BasicImage;
import com.evertrip.member.entity.Member;
import com.evertrip.post.dto.request.PostPatchDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name="post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name="title")
    private String title;

    @Column(name="view")
    private Long view;

    @Column(name="like_count")
    private Long likeCount;

    @Column(name="deleted_yn")
    private Boolean deletedYn;

    @Column(name="post_image")
    private String profileImage;

    @Column(name="deleted_at")
    private LocalDateTime deletedAt;

    public Post(Member member, String title) {
        this.member = member;
        this.title = title;
        this.deletedYn = false;
        this.profileImage = BasicImage.BASIC_POST_IMAGE.getPath();
        this.likeCount = 0L;
        this.view = 0L;
    }

    public Post(Member member, String title, String profileImage) {
        this.member = member;
        this.title = title;
        this.deletedYn = false;
        this.profileImage = profileImage;
        this.likeCount = 0L;
        this.view = 0L;
    }


    public void deletePost() {
        this.deletedYn = true;
        this.deletedAt =  LocalDateTime.now();
    }

    public void updatePost(String title,String profileImage) {
        this.title = title;
        this.profileImage = profileImage;
    }

}
