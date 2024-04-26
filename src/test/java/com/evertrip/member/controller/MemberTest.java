package com.evertrip.member.controller;

import com.evertrip.file.common.TableName;
import com.evertrip.file.dto.request.FileRequestDto;
import com.evertrip.file.repository.FileRepository;
import com.evertrip.file.service.FileService;
import com.evertrip.member.dto.request.MemberProfilePatchDto;
import com.evertrip.member.dto.response.MemberProfileResponseDto;
import com.evertrip.member.entity.Member;
import com.evertrip.member.entity.Role;
import com.evertrip.member.repository.MemberDetailRepository;
import com.evertrip.member.repository.MemberProfileRepository;
import com.evertrip.member.repository.MemberRepository;
import com.evertrip.member.repository.RoleRepository;
import com.evertrip.security.redis.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
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
    MemberDetailRepository memberDetailRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    FileService fileService;

    @MockBean
    RedisService redisService;

    private static String token = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyIiwiZXhwIjoxNzE2NjE5MjAyfQ.K1z5xJJsthPZI3BUqSjbs8sa-l7gwaNVAc5NO_CoPtSl-cs18o67J4UpWykqnA-Q0NerEYt9vM7mM1tbzCdcuQ";

    private static String refreshTokenKey = "refresh:SMKUt25uIr6opwVA7FbDyZGEYSQzUILOp3LLtXskm36c90/3sOMVGV0w62ReY3u1MjsKZkgZi0E7kTmks7/joA==_2";

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

    @DisplayName("회원 프로필 수정 BindingResult 검증 테스트")
    @Test
    public void modifyMemberProfileValidTest() throws Exception {
        // given
        MemberProfilePatchDto inAppropriateNickname = new MemberProfilePatchDto("닉네임", "테스트를 위한 프로필 수정입니다. 참고해주시면 감사하겠습니다", 1L, null);
        MemberProfilePatchDto inAppropriateDescription = new MemberProfilePatchDto("닉네임테스트", "부적절한설명", 1L, null);

        // 요청 메시지 바디에 JSON 형태로 넣어주기 위해 객체 직렬화 합니다.
        String inAppropriateNicknameJson = objectMapper.writeValueAsString(inAppropriateNickname);
        String inAppropriateDescriptionJson = objectMapper.writeValueAsString(inAppropriateDescription);

        // when & then
        // 잘못된 형식의 닉네임을 넣어줬을 때 제대로 BindingResult의 유효성 검사를 진행하는지 테스트합니다.
        mockMvc.perform(patch("/members/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(inAppropriateNicknameJson))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorResponse.errorName").value("INCORRECT_FORMAT_NICKNAME"));

        // 잘못된 형식의 description을 넣어줬을 때 제대로 BindingResult의 유효성 검사를 진행하는지 테스트합니다.
        mockMvc.perform(patch("/members/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(inAppropriateDescriptionJson))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorResponse.errorName").value("INCORRECT_FORMAT_DESCRIPTION"));

    }

    @DisplayName("회원 프로필 수정 Invalid fileId 테스트")
    @Test
    public void modifyMemberProfileFailTest() throws Exception {
        // given
        MemberProfilePatchDto invalidFileInProfile = new MemberProfilePatchDto("수정할닉네임", "테스트를 위한 프로필 수정입니다. 참고해주시면 감사하겠습니다", 100L, null);


        // 요청 메시지 바디에 JSON 형태로 넣어주기 위해 객체 직렬화 합니다.
        String invalidFileInProfileJson = objectMapper.writeValueAsString(invalidFileInProfile);

        // when & then
        // 잘못된 형식의 닉네임을 넣어줬을 때 제대로 BindingResult의 유효성 검사를 진행하는지 테스트합니다.
        mockMvc.perform(patch("/members/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(invalidFileInProfileJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorResponse.errorName").value("FILE_NOT_FOUND"));

    }

    @DisplayName("회원 프로필 수정 정상 동작 테스트")
    @Test
    public void modifyMemberProfileTest() throws Exception {
        // given
        MemberProfilePatchDto profile = new MemberProfilePatchDto("수정할닉네임", "테스트를 위한 프로필 수정입니다. 참고해주시면 감사하겠습니다", 1L, null);
        String profileImage = fileRepository.findById(1L).get().getPath();

        // 요청 메시지 바디에 JSON 형태로 넣어주기 위해 객체 직렬화 합니다.
        String profileJson = objectMapper.writeValueAsString(profile);

        // when & then
        // 잘못된 형식의 닉네임을 넣어줬을 때 제대로 BindingResult의 유효성 검사를 진행하는지 테스트합니다.
        mockMvc.perform(patch("/members/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(profileJson))
                .andExpect(status().isOk());

        // 변경된 이후의 회원 프로필을 DB에서 뽑아옵니다.
        MemberProfileResponseDto modifyProfile = memberProfileRepository.findMemberProfiles(2L, false).get(0);


        // 비교하기
        assertEquals(profile.getNickName(), modifyProfile.getNickName());
        assertEquals(profile.getDescription(), modifyProfile.getDescription());
        assertEquals(profileImage, modifyProfile.getProfileImage());
    }

    @DisplayName("로그아웃 테스트")
    @Test
    public void logoutTest() throws Exception {
        // given
        // Mock 객체 설정 - redisService 목 객체에 getRefreshToken() 메소드를 호출할 시 Null 값을 반환하도록 설정
        Mockito.when(redisService.getRefreshToken(anyString())).thenReturn(null);


        // when
        mockMvc.perform(get("/removeToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isOk());

        // then
        // Mock 객체 호출 확인 - verify를 이용하여 redisService의 removeRefreshToken(refreshTokenKey)가 실제로 호출되었는지 확인합니다.
        // 실제로는 IP 주소가 계속 바뀌므로 IP 주소는 동일하다는 가정하에 removeRefreshToken이 호출되었는지에 대한 테스트를 진행하였습니다.
        Mockito.verify(redisService).removeRefreshToken(anyString());
        // Mock 객체 설정에 의해 Null 값 반환 확인하는 작업입니다.
        assertNull(redisService.getRefreshToken(refreshTokenKey));
    }

    @DisplayName("회원 탈퇴 테스트")
    @Test
    public void updateMemberDeleteTest() throws Exception {
        // given
        // Mock 객체 설정 - redisService 목 객체에 getRefreshToken() 메소드를 호출할 시 Null 값을 반환하도록 설정
        Mockito.when(redisService.getRefreshToken(anyString())).thenReturn(null);

        Member member = memberRepository.findById(2L).get();


        // when
        mockMvc.perform(delete("/members")
                        .header("Authorization", token))
                .andExpect(status().isOk());

        // then

        // 회원, 프로필, 상세에 대해 softDelete 설정 했는지에 대한 테스트
        Member findMember = memberRepository.findById(member.getId()).get();
        assertEquals(true,findMember.isDeletedYn());
        assertEquals(true,memberProfileRepository.findByMemberId(member.getId(),false).isEmpty());
        assertEquals(true,memberDetailRepository.findByMemberId(member.getId(),false).isEmpty());

        // Redis에 토큰 제거 테스트
        Mockito.verify(redisService).removeRefreshToken(anyString());
        assertNull(redisService.getRefreshToken(refreshTokenKey));

        //  회원 프로필 이미지 삭제했는지 확인 테스트
        assertEquals(true,fileService.findFilesByTableInfo(FileRequestDto.create(TableName.MEMBER_PROFILE, memberProfileRepository.findByMemberId(member.getId(), true).get().getId()), false).isEmpty());


        // 회원이 등록한 게시글 및 게시글 관련 정보 삭제 테스트


    }



}