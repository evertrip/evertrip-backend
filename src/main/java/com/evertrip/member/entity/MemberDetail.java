package com.evertrip.member.entity;

import com.evertrip.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
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

    @Column(name="address")
    private String address;

    @Column(name="deleted_yn")
    private boolean deletedYn;


}
