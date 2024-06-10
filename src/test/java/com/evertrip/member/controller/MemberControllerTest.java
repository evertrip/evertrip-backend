package com.evertrip.member.controller;

import com.evertrip.common.RestDocsTestSupport;
import com.evertrip.file.common.TableName;
import com.evertrip.file.dto.request.FileRequestDto;
import com.evertrip.file.repository.FileRepository;
import com.evertrip.file.service.FileService;
import com.evertrip.member.dto.request.MemberProfilePatchDto;
import com.evertrip.member.dto.response.MemberProfileResponseDto;
import com.evertrip.member.entity.Member;
import com.evertrip.member.repository.MemberDetailRepository;
import com.evertrip.member.repository.MemberProfileRepository;
import com.evertrip.member.repository.MemberRepository;
import com.evertrip.member.repository.RoleRepository;
import com.evertrip.security.jwt.SymmetricCrypto;
import com.evertrip.security.redis.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureRestDocs
@Transactional
@DirtiesContext
class MemberControllerTest extends RestDocsTestSupport {

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

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private  SymmetricCrypto crypto;

    private String email = "gdFNXOUc7A0aYDF8+/pIJ/EU/4kHEon7QKcTWllM2wA=";

    private DateTimeFormatter formatter;

    @BeforeEach
    public void set() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        this.formatter = formatter;
    }



    @DisplayName("회원 프로필 조회 테스트")
    @Test
    public void getmemberprofile_200() throws Exception {
        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // when
        // pk가 2L인 사용자의 프로필을 조회합니다. 헤더의 토큰은 해당 pk의 사용자 토큰입니다.
        List<MemberProfileResponseDto> memberProfiles = memberProfileRepository.findMemberProfiles(2L, false);
        MemberProfileResponseDto memberProfile = memberProfiles.get(0);


        // then
        // jsonPath를 이용하여 컨트롤러가 주는 HTTP 응답 바디에 접근하여 값을 비교하는 코드입니다.
        mockMvc.perform(get("/api/members")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.errorResponse").value(nullValue()))
                .andExpect(jsonPath("$.content.memberId").value(memberProfile.getMemberId()))
                .andExpect(jsonPath("$.content.nickName").value(memberProfile.getNickName()))
                .andExpect(jsonPath("$.content.description").value(memberProfile.getDescription()))
                .andExpect(jsonPath("$.content.createdAt").value(memberProfile.getCreatedAt().format(formatter)))
                .andExpect(jsonPath("$.content.updatedAt").value(memberProfile.getUpdatedAt().format(formatter)))
                .andExpect(jsonPath("$.content.profileImage").value(memberProfile.getProfileImage()))
                .andExpect(jsonPath("$.content.email").value(crypto.decrypt(memberProfile.getEmail())))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("success")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("API 성공 여부"),
                                fieldWithPath("content")
                                        .type(JsonFieldType.OBJECT)
                                        .description("응답 내용"),
                                fieldWithPath("content.memberId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("멤버 고유 번호"),
                                fieldWithPath("content.nickName")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 닉네임"),
                                fieldWithPath("content.description")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 자기소개글"),
                                fieldWithPath("content.createdAt")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 생성 날짜"),
                                fieldWithPath("content.updatedAt")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 수정 날짜"),
                                fieldWithPath("content.profileImage")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 프로필 이미지 주소"),
                                fieldWithPath("content.email")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 이메일"),
                                fieldWithPath("errorResponse")
                                        .type(JsonFieldType.NULL)
                                        .description("에러 응답, 에러가 없는 경우 null")
                        )
                ));

    }

    @DisplayName("회원 프로필 수정 테스트")
    @Test
    public void modifymemberprofile_200() throws Exception {
        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        MemberProfilePatchDto profile = new MemberProfilePatchDto("수정할닉네임", "테스트를 위한 프로필 수정입니다. 참고해주시면 감사하겠습니다", 1L, "https://ever-trip-bucket.s3.ap-northeast-2.amazonaws.com/basic_user_image.jpg");
        String profileImage = fileRepository.findById(1L).get().getPath();

        // 요청 메시지 바디에 JSON 형태로 넣어주기 위해 객체 직렬화 합니다.
        String profileJson = objectMapper.writeValueAsString(profile);

        // when & then
        mockMvc.perform(patch("/api/members/profile")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .contentType(APPLICATION_JSON)
                        .content(profileJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.errorResponse").value(nullValue()))
                .andExpect(jsonPath("$.content.memberId").value(userDetails.getUsername()))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        requestFields( // 요청 필드 추가
                            fieldWithPath("nickName")
                                    .type(JsonFieldType.STRING)
                                    .description("수정하고 싶은 닉네임")
                                    .attributes(constraints("닉네임 형식은 한글,영문 대소문자, 숫자로 이루어지며 최소 5글자 이상 최대 15글자 이하입니다.")),
                            fieldWithPath("description")
                                    .type(JsonFieldType.STRING)
                                    .description("수정하고 싶은 자기 소개글")
                                    .attributes(constraints("자기 소개글은 최소 20글자 이상 최대 500글자 이하입니다.")),
                            fieldWithPath("fileId")
                                    .type(JsonFieldType.NUMBER)
                                    .description("수정하고 싶은 파일 pk")
                                    .attributes(constraints("NULL일 경우 기존 이미지 사용")),
                            fieldWithPath("profileImage")
                                    .type(JsonFieldType.STRING)
                                    .description("수정하고 싶은 프로필 이미지 주소")
                                    .attributes(constraints("NULL일 경우 사용자 기본 이미지 사용"))
                                ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("success")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("API 성공 여부"),
                                fieldWithPath("content")
                                        .type(JsonFieldType.OBJECT)
                                        .description("응답 내용"),
                                fieldWithPath("content.memberId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("수정된 멤버 고유 번호"),
                                fieldWithPath("errorResponse")
                                        .type(JsonFieldType.NULL)
                                        .description("에러 응답, 에러가 없는 경우 NULL")
                        )
                ));

        // 변경된 이후의 회원 프로필을 DB에서 뽑아옵니다.
        MemberProfileResponseDto modifyProfile = memberProfileRepository.findMemberProfiles(2L, false).get(0);


        // 비교하기
        assertEquals(profile.getNickName(), modifyProfile.getNickName());
        assertEquals(profile.getDescription(), modifyProfile.getDescription());
        assertEquals(profileImage, modifyProfile.getProfileImage());
    }

    @DisplayName("로그아웃 테스트")
    @Test
    public void removetoken_200() throws Exception {
        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        System.out.println("getUserName() :" + userDetails.getUsername());
        // Mock 객체 설정 - redisService 목 객체에 getRefreshToken() 메소드를 호출할 시 Null 값을 반환하도록 설정
        Mockito.when(redisService.getRefreshToken(Mockito.anyString())).thenReturn(null);

        // then
        mockMvc.perform(get("/api/removeToken")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.errorResponse").value(nullValue()))
                .andExpect(jsonPath("$.content.memberId").value(2L))
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("success")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("API 성공 여부"),
                                fieldWithPath("content")
                                        .type(JsonFieldType.OBJECT)
                                        .description("응답 내용"),
                                fieldWithPath("content.memberId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("멤버 고유 번호"),
                                fieldWithPath("errorResponse")
                                        .type(JsonFieldType.NULL)
                                        .description("에러 응답, 에러가 없는 경우 NULL")
                        )
                ));

        // Mock 객체 호출 확인 - verify를 이용하여 redisService의 removeRefreshToken(refreshTokenKey)가 실제로 호출되었는지 확인합니다.
        // 실제로는 IP 주소가 계속 바뀌므로 IP 주소는 동일하다는 가정하에 removeRefreshToken이 호출되었는지에 대한 테스트를 진행하였습니다.
        Mockito.verify(redisService).removeRefreshToken(anyString());
        // Mock 객체 설정에 의해 Null 값 반환 확인하는 작업입니다.
        assertNull(redisService.getRefreshToken("refreshTokenKey"));
    }

    @DisplayName("회원 탈퇴")
    @Test
    public void updatememberdelete_200() throws Exception {
        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        // Mock 객체 설정 - redisService 목 객체에 getRefreshToken() 메소드를 호출할 시 Null 값을 반환하도록 설정
        Mockito.when(redisService.getRefreshToken(anyString())).thenReturn(null);

        Member member = memberRepository.findById(2L).get();


        // when
        mockMvc.perform(delete("/api/members")
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.errorResponse").value(nullValue()))
                .andExpect(jsonPath("$.content.memberId").value(member.getId()))
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("success")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("API 성공 여부"),
                                fieldWithPath("content")
                                        .type(JsonFieldType.OBJECT)
                                        .description("응답 내용"),
                                fieldWithPath("content.memberId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("탈퇴된 멤버 고유 번호"),
                                fieldWithPath("errorResponse")
                                        .type(JsonFieldType.NULL)
                                        .description("에러 응답, 에러가 없는 경우 NULL")
                        )
                ));

        // then

        // 회원, 프로필, 상세에 대해 softDelete 설정 했는지에 대한 테스트
        Member findMember = memberRepository.findById(member.getId()).get();
        assertEquals(true,findMember.isDeletedYn());
        assertEquals(true,memberProfileRepository.findByMemberId(member.getId(),false).isEmpty());
        assertEquals(true,memberDetailRepository.findByMemberId(member.getId(),false).isEmpty());

        // Redis에 토큰 제거 테스트
        Mockito.verify(redisService).removeRefreshToken(anyString());
        assertNull(redisService.getRefreshToken(anyString()));

        //  회원 프로필 이미지 삭제했는지 확인 테스트
        assertEquals(true,fileService.findFilesByTableInfo(FileRequestDto.create(TableName.MEMBER_PROFILE, memberProfileRepository.findByMemberId(member.getId(), true).get().getId()), false).isEmpty());


        // 회원이 등록한 게시글 및 게시글 관련 정보 삭제 테스트


    }



}