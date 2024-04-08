package com.evertrip.member.entity;

import com.evertrip.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDetail extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_detail_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name="name")
    private String name;

    @Column(name="phone")
    private String phone;

    @Column(name="gender")
    private String gender;

    @Column(name="age")
    private String age;

    @Column(name="deleted_yn")
    private boolean deletedYn;

    public MemberDetail(Member member, String name, String phone, String gender, String age) {
        this.member = member;
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.age = age;
        this.deletedYn = false;
    }

    public void softDelete() {
        this.deletedYn = true;
    }
}
