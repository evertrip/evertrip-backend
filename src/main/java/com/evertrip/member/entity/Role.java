package com.evertrip.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Column(name = "role_name")
    @Enumerated(EnumType.STRING)
    private RoleName roleName;

    private enum RoleName {
        ADMIN("관리자"), USER("일반사용자");

        private final String roleName;
        RoleName(String roleName) {
            this.roleName = roleName;
        }

        String getRoleName() {
            return roleName;
        }
    }
}
