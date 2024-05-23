package com.evertrip.post.entity;

import com.evertrip.constant.ConstantPool;
import com.evertrip.post.dto.sqs.PostLogDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class PostLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="post_log_id")
    private Long id;

    @Column(name="member_id")
    private Long memberId;

    @Column(name="post_id")
    private Long postId;

    @Enumerated(EnumType.STRING)
    @Column(name="event_type")
    private ConstantPool.EventType eventType;

    @Column(name="event_content")
    private String eventContent;

    @Column(name="deleted_yn")
    private boolean deletedYn;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    public PostLog(PostLogDto dto) {
        this.memberId = dto.getMemberId();
        this.postId = dto.getPostId();
        this.createdAt = dto.getCreatedAt();
        this.eventContent = dto.getEventContent();
        this.eventType = dto.getEventType();
        this.deletedYn = false;
    }




}
