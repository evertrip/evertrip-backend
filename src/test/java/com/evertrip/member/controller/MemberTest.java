package com.evertrip.member.controller;

import com.evertrip.member.entity.Member;
import com.evertrip.member.entity.Role;
import com.evertrip.member.repository.MemberRepository;
import com.evertrip.member.repository.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class MemberTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    MockMvc mockMvc;

    @DisplayName("회원 권한 테스트")
    @Test
    public void memberAuthorityTest() throws Exception {
        // given
        Role userRole = roleRepository.findByRoleName(Role.RoleName.USER).get();
        Role adminRole = roleRepository.findByRoleName(Role.RoleName.ADMIN).get();

        Member testMember = memberRepository.findById(2L).get();

        // when
        String originalRole = testMember.getRole().getRoleName().name();

        // 기존 회원의 권한은 일반 사용자입니다.
        assertEquals(originalRole, "USER");

        //then
        // 일반 사용자의 경우 /admin에 대한 요청에 403 권한 에러가 발생하여야 합니다.
        mockMvc.perform(get("/admin/test1").header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyIiwiZXhwIjoxNzE1NDcyMDE4fQ.sfnp2mG6mhZV7wezNaL1YVvZadMNT8U8mT6wy2FST4-0ZkvQ1xfw2oEE-EbWAgTJQZ7-82HpNs_90RI5gaoYWg"))
                .andExpect(status().isForbidden());


        // when
        // 기존 회원의 권한을 관리자로 바꿔봅니다.
        testMember.changeRole(adminRole);

        // then
        // /admin에 대한 요청 시 관리자로 권한을 바꿨기 때문에 통과되어야 합니다.
        mockMvc.perform(get("/admin/test1").header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyIiwiZXhwIjoxNzE1NDcyMDE4fQ.sfnp2mG6mhZV7wezNaL1YVvZadMNT8U8mT6wy2FST4-0ZkvQ1xfw2oEE-EbWAgTJQZ7-82HpNs_90RI5gaoYWg"))
                .andExpect((status().isOk()));

    }

}