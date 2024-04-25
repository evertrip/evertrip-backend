package com.evertrip.member.controller;

import com.evertrip.member.dto.response.MemberProfileResponseDto;
import com.evertrip.member.entity.Member;
import com.evertrip.member.entity.Role;
import com.evertrip.member.repository.MemberProfileRepository;
import com.evertrip.member.repository.MemberRepository;
import com.evertrip.member.repository.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    MemberProfileRepository memberProfileRepository;

    @Autowired
    RoleRepository roleRepository;

    private static String token = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyIiwiZXhwIjoxNzE1NDcyMDE4fQ.sfnp2mG6mhZV7wezNaL1YVvZadMNT8U8mT6wy2FST4-0ZkvQ1xfw2oEE-EbWAgTJQZ7-82HpNs_90RI5gaoYWg";

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
        mockMvc.perform(get("/admin/test1").header("Authorization", token))
                .andExpect(status().isForbidden());


        // when
        // 기존 회원의 권한을 관리자로 바꿔봅니다.
        testMember.changeRole(adminRole);

        // then
        // /admin에 대한 요청 시 관리자로 권한을 바꿨기 때문에 통과되어야 합니다.
        mockMvc.perform(get("/admin/test1").header("Authorization", token))
                .andExpect((status().isOk()));

    }

    @DisplayName("회원 프로필 조회 테스트")
    @Test
    public void getMemberProfileTest() throws Exception {
        // given

        // when
        // pk가 2L인 사용자의 프로필을 조회합니다. 헤더의 토큰은 해당 pk의 사용자 토큰입니다.
        List<MemberProfileResponseDto> memberProfiles = memberProfileRepository.findMemberProfiles(2L, false);
        MemberProfileResponseDto memberProfile = memberProfiles.get(0);


        // then
        // jsonPath를 이용하여 컨트롤러가 주는 HTTP 응답 바디에 접근하여 값을 비교하는 코드입니다.
        mockMvc.perform(get("/members")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].memberId").value(memberProfile.getMemberId()))
                .andExpect(jsonPath("$.content[0].nickName").value(memberProfile.getNickName()))
                .andExpect(jsonPath("$.content[0].description").value(memberProfile.getDescription()))
                .andExpect(jsonPath("$.content[0].createdAt").value(memberProfile.getCreatedAt()))
                .andExpect(jsonPath("$.content[0].updatedAt").value(memberProfile.getUpdatedAt()))
                .andExpect(jsonPath("$.content[0].profileImage").value(memberProfile.getProfileImage()));
    }


}