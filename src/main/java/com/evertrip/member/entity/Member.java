package com.evertrip.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="role_id")
    private Role role;

    @Column(name = "email")
    private String email;

    @Column(name= "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_yn")
    private boolean deletedYn;

    public Member(String email, Role role) {
        this.email = email;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.deletedYn = false;
    }

    public void softDelete() {
        this.deletedYn = true;
    }
}
