package com.evertrip.post.controller;

import com.evertrip.common.RestDocsTestSupport;
import com.evertrip.constant.ConstantPool;
import com.evertrip.member.entity.Member;
import com.evertrip.member.repository.MemberRepository;
import com.evertrip.post.dto.sqs.PostLogDto;
import com.evertrip.post.entity.Post;
import com.evertrip.post.entity.PostLog;
import com.evertrip.post.repository.PostLogRepository;
import com.evertrip.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureRestDocs
@Transactional
@DirtiesContext
class PostLogControllerTest extends RestDocsTestSupport {

    @Autowired
    private PostLogRepository postLogRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    public Post post;


    @Autowired
    private UserDetailsService userDetailsService;

    private String email1 = "gdFNXOUc7A0aYDF8+/pIJ/EU/4kHEon7QKcTWllM2wA=";

    private String email2 = "V8KeyURaOLtc1DTcGil+VZIFwHDTotGtwz59lijSHsI=";

    private String email3 = "Q9/5GNmZfASbmFw+d5lE+EDPIy2+3x4grC7SD84qOFo=";

    @BeforeEach
    public void setUp() {

        Member member1 = memberRepository.findByEmail(email1)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Member member2 = memberRepository.findByEmail(email2)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Member member3 = memberRepository.findByEmail(email3)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        post = new Post(member1, "Test Post");
        postRepository.save(post);
        this.post = post;

        List<PostLog> postLogs = Arrays.asList(
                new PostLog(new PostLogDto(member3.getId(), post.getId(), LocalDateTime.of(2024, 5, 9, 13, 13), ConstantPool.EventType.VIEWER, "MEMBER IS VISITED")),
                new PostLog(new PostLogDto(member2.getId(), post.getId(), LocalDateTime.of(2024, 4, 9, 13, 13), ConstantPool.EventType.VIEWER, "MEMBER IS VISITED")),
                new PostLog(new PostLogDto(member2.getId(), post.getId(), LocalDateTime.of(2024, 4, 30, 13, 13), ConstantPool.EventType.VIEWER, "MEMBER IS VISITED")),
                new PostLog(new PostLogDto(member2.getId(), post.getId(), LocalDateTime.of(2024, 5, 9, 13, 13), ConstantPool.EventType.VIEWER, "MEMBER IS VISITED")),
                new PostLog(new PostLogDto(member2.getId(), post.getId(), LocalDateTime.of(2024, 6, 9, 13, 13), ConstantPool.EventType.VIEWER, "MEMBER IS VISITED")),
                new PostLog(new PostLogDto(member2.getId(), post.getId(), LocalDateTime.of(2023, 12, 9, 13, 13), ConstantPool.EventType.VIEWER, "MEMBER IS VISITED")),
                new PostLog(new PostLogDto(member2.getId(), post.getId(), LocalDateTime.of(2024, 2, 9, 13, 13), ConstantPool.EventType.VIEWER, "MEMBER IS VISITED")),
                new PostLog(new PostLogDto(member2.getId(), post.getId(), LocalDateTime.of(2024, 3, 9, 13, 13), ConstantPool.EventType.VIEWER, "MEMBER IS VISITED")),
                new PostLog(new PostLogDto(member2.getId(), post.getId(), LocalDateTime.of(2024, 6, 9, 13, 13), ConstantPool.EventType.VIEWER, "MEMBER IS VISITED")),
                new PostLog(new PostLogDto(member2.getId(), post.getId(), LocalDateTime.of(2024, 2, 9, 13, 13), ConstantPool.EventType.VIEWER, "MEMBER IS VISITED")),
                new PostLog(new PostLogDto(member1.getId(), post.getId(), LocalDateTime.of(2024, 1, 9, 13, 13), ConstantPool.EventType.VIEWER, "MEMBER IS VISITED")),
                new PostLog(new PostLogDto(member1.getId(), post.getId(), LocalDateTime.of(2023, 5, 9, 13, 13), ConstantPool.EventType.VIEWER, "MEMBER IS VISITED")),
                new PostLog(new PostLogDto(member3.getId(), post.getId(), LocalDateTime.of(2023, 8, 9, 13, 13), ConstantPool.EventType.VIEWER, "MEMBER IS VISITED")),
                new PostLog(new PostLogDto(member1.getId(), post.getId(), LocalDateTime.of(2024, 4, 9, 13, 13), ConstantPool.EventType.VIEWER, "MEMBER IS VISITED")),
                new PostLog(new PostLogDto(member1.getId(), post.getId(), LocalDateTime.of(2024, 6, 9, 13, 13), ConstantPool.EventType.VIEWER, "MEMBER IS VISITED")),
                new PostLog(new PostLogDto(member3.getId(), post.getId(), LocalDateTime.of(2023, 12, 9, 13, 13), ConstantPool.EventType.VIEWER, "MEMBER IS VISITED")),
                new PostLog(new PostLogDto(member1.getId(), post.getId(), LocalDateTime.of(2023, 8, 9, 13, 13), ConstantPool.EventType.VIEWER, "MEMBER IS VISITED")),
                new PostLog(new PostLogDto(member3.getId(), post.getId(), LocalDateTime.of(2024, 5, 9, 13, 13), ConstantPool.EventType.VIEWER, "MEMBER IS VISITED")),
                new PostLog(new PostLogDto(member2.getId(), post.getId(), LocalDateTime.of(2024, 5, 9, 13, 13), ConstantPool.EventType.SCROLL, "30")),
                new PostLog(new PostLogDto(member1.getId(), post.getId(), LocalDateTime.of(2024, 5, 11, 13, 13), ConstantPool.EventType.SCROLL, "50")),
                new PostLog(new PostLogDto(member3.getId(), post.getId(), LocalDateTime.of(2024, 5, 15, 13, 13), ConstantPool.EventType.SCROLL, "10")),
                new PostLog(new PostLogDto(member2.getId(), post.getId(), LocalDateTime.of(2024, 3, 15, 13, 13), ConstantPool.EventType.SCROLL, "50")),
                new PostLog(new PostLogDto(member1.getId(), post.getId(), LocalDateTime.of(2024, 2, 15, 13, 13), ConstantPool.EventType.SCROLL, "30")),
                new PostLog(new PostLogDto(member3.getId(), post.getId(), LocalDateTime.of(2024, 3, 17, 13, 13), ConstantPool.EventType.SCROLL, "80")),
                new PostLog(new PostLogDto(member2.getId(), post.getId(), LocalDateTime.of(2024, 1, 17, 13, 13), ConstantPool.EventType.SCROLL, "25")),
                new PostLog(new PostLogDto(member1.getId(), post.getId(), LocalDateTime.of(2023, 5, 17, 13, 13), ConstantPool.EventType.SCROLL, "85")),
                new PostLog(new PostLogDto(member3.getId(), post.getId(), LocalDateTime.of(2023, 8, 17, 13, 13), ConstantPool.EventType.SCROLL, "75")),
                new PostLog(new PostLogDto(member3.getId(), post.getId(), LocalDateTime.of(2023, 9, 17, 13, 13), ConstantPool.EventType.STAYING, "700")),
                new PostLog(new PostLogDto(member2.getId(), post.getId(), LocalDateTime.of(2023, 9, 12, 13, 14), ConstantPool.EventType.STAYING, "50")),
                new PostLog(new PostLogDto(member1.getId(), post.getId(), LocalDateTime.of(2024, 3, 12, 13, 14), ConstantPool.EventType.STAYING, "750")),
                new PostLog(new PostLogDto(member3.getId(), post.getId(), LocalDateTime.of(2024, 5, 12, 13, 14), ConstantPool.EventType.STAYING, "20")),
                new PostLog(new PostLogDto(member2.getId(), post.getId(), LocalDateTime.of(2024, 5, 12, 13, 14), ConstantPool.EventType.HISTORY, "CREATE_COMMENT")),
                new PostLog(new PostLogDto(member3.getId(), post.getId(), LocalDateTime.of(2024, 5, 18, 13, 14), ConstantPool.EventType.HISTORY, "CREATE_COMMENT")),
                new PostLog(new PostLogDto(member2.getId(), post.getId(), LocalDateTime.of(2024, 6, 2, 13, 14), ConstantPool.EventType.HISTORY, "CREATE_LIKE")),
                new PostLog(new PostLogDto(member1.getId(), post.getId(), LocalDateTime.of(2024, 4, 2, 13, 14), ConstantPool.EventType.HISTORY, "CREATE_LIKE")),
                new PostLog(new PostLogDto(member3.getId(), post.getId(), LocalDateTime.of(2024, 4, 27, 13, 14), ConstantPool.EventType.HISTORY, "DELETE_LIKE")),
                new PostLog(new PostLogDto(member2.getId(), post.getId(), LocalDateTime.of(2024, 5, 31, 13, 14), ConstantPool.EventType.HISTORY, "DELETE_COMMENT"))
        );

        postLogRepository.saveAll(postLogs);
    }

    @DisplayName("게시글 총 조회수 조회")
    @Test
    public void gettotalviews_200() throws Exception {
        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email1);


        // when & then
        mockMvc.perform(get("/api/post-logs/{post-id}/views",post.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.errorResponse").value(nullValue()))
                .andExpect(jsonPath("$.content").value(0))
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
                                        .type(JsonFieldType.NUMBER)
                                        .description("게시글 총 조회수"),
                                fieldWithPath("errorResponse")
                                        .type(JsonFieldType.NULL)
                                        .description("에러 응답, 에러가 없는 경우 NULL")
                        )
                ));


    }

    @DisplayName("게시글 최근 방문자 수 조회")
    @Test
    public void getnumberofvisitors_200() throws Exception {
        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email1);


        // when & then
        mockMvc.perform(get("/api/post-logs/{post-id}/visitors",post.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.errorResponse").value(nullValue()))
                .andExpect(jsonPath("$.content[0].visitors").value(1L))
                .andExpect(jsonPath("$.content[0].period").value("202305"))
                .andExpect(jsonPath("$.content[1].visitors").value(2L))
                .andExpect(jsonPath("$.content[1].period").value("202308"))
                .andExpect(jsonPath("$.content[2].visitors").value(2L))
                .andExpect(jsonPath("$.content[2].period").value("202312"))
                .andExpect(jsonPath("$.content[3].visitors").value(1L))
                .andExpect(jsonPath("$.content[3].period").value("202401"))
                .andExpect(jsonPath("$.content[4].visitors").value(2L))
                .andExpect(jsonPath("$.content[4].period").value("202402"))
                .andExpect(jsonPath("$.content[5].visitors").value(1L))
                .andExpect(jsonPath("$.content[5].period").value("202403"))
                .andExpect(jsonPath("$.content[6].visitors").value(3L))
                .andExpect(jsonPath("$.content[6].period").value("202404"))
                .andExpect(jsonPath("$.content[7].visitors").value(3L))
                .andExpect(jsonPath("$.content[7].period").value("202405"))
                .andExpect(jsonPath("$.content[8].visitors").value(18L))
                .andExpect(jsonPath("$.content[8].period").value("total"))
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
                                        .type(JsonFieldType.ARRAY)
                                        .description("응답 내용"),
                                fieldWithPath("content[].visitors")
                                        .type(JsonFieldType.NUMBER)
                                        .description("월별 방문자수"),
                                fieldWithPath("content[].period")
                                        .type(JsonFieldType.STRING)
                                        .description("해당 년월(전체는 total)"),
                                fieldWithPath("errorResponse")
                                        .type(JsonFieldType.NULL)
                                        .description("에러 응답, 에러가 없는 경우 NULL")
                        )
                ));


    }

    @DisplayName("게시글 최근 방문 회원 HISTORY 조회")
    @Test
    public void getvisitorshistory_200() throws Exception {
        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email1);


        // when & then
        mockMvc.perform(get("/api/post-logs/{post-id}/visitors/history",post.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.errorResponse").value(nullValue()))
                .andExpect(jsonPath("$.content[0].memberId").value(1L))
                .andExpect(jsonPath("$.content[0].nickName").value("박창우"))
                .andExpect(jsonPath("$.content[0].profileImage").value("https://ever-trip-bucket.s3.ap-northeast-2.amazonaws.com/basic_user_image.jpg"))
                .andExpect(jsonPath("$.content[0].eventCreatedAt").value("2024/06/09 13:13:00"))
                .andExpect(jsonPath("$.content[17].memberId").value(2L))
                .andExpect(jsonPath("$.content[17].nickName").value("김강욱"))
                .andExpect(jsonPath("$.content[17].profileImage").value("https://ever-trip-bucket.s3.ap-northeast-2.amazonaws.com/stock-696x365.jpg_7f156777-707d-4763-8531-dbb9bfe9c4bc"))
                .andExpect(jsonPath("$.content[17].eventCreatedAt").value("2023/05/09 13:13:00"))
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
                                        .type(JsonFieldType.ARRAY)
                                        .description("응답 내용"),
                                fieldWithPath("content[].memberId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("멤버 고유 번호"),
                                fieldWithPath("content[].nickName")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 닉네임"),
                                fieldWithPath("content[].profileImage")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 프로필 이미지 주소"),
                                fieldWithPath("content[].eventCreatedAt")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 방문 날짜 일시"),
                                fieldWithPath("errorResponse")
                                        .type(JsonFieldType.NULL)
                                        .description("에러 응답, 에러가 없는 경우 NULL")
                        )
                ));


    }

    @DisplayName("게시글 평균 스크롤 깊이 조회")
    @Test
    public void getvisitorsscroll_200() throws Exception {
        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email1);


        // when & then
        mockMvc.perform(get("/api/post-logs/{post-id}/scroll",post.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.errorResponse").value(nullValue()))
                .andExpect(jsonPath("$.content[0].scroll").value(85L))
                .andExpect(jsonPath("$.content[0].period").value("202305"))
                .andExpect(jsonPath("$.content[1].scroll").value(75L))
                .andExpect(jsonPath("$.content[1].period").value("202308"))
                .andExpect(jsonPath("$.content[2].scroll").value(25L))
                .andExpect(jsonPath("$.content[2].period").value("202401"))
                .andExpect(jsonPath("$.content[3].scroll").value(30L))
                .andExpect(jsonPath("$.content[3].period").value("202402"))
                .andExpect(jsonPath("$.content[4].scroll").value(65L))
                .andExpect(jsonPath("$.content[4].period").value("202403"))
                .andExpect(jsonPath("$.content[5].scroll").value(30L))
                .andExpect(jsonPath("$.content[5].period").value("202405"))
                .andExpect(jsonPath("$.content[6].scroll").value(48L))
                .andExpect(jsonPath("$.content[6].period").value("total"))
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
                                        .type(JsonFieldType.ARRAY)
                                        .description("응답 내용"),
                                fieldWithPath("content[].scroll")
                                        .type(JsonFieldType.NUMBER)
                                        .description("월별 평균 스크롤 깊이(%)"),
                                fieldWithPath("content[].period")
                                        .type(JsonFieldType.STRING)
                                        .description("해당 년월(전체는 total)"),
                                fieldWithPath("errorResponse")
                                        .type(JsonFieldType.NULL)
                                        .description("에러 응답, 에러가 없는 경우 NULL")
                        )
                ));


    }

    @DisplayName("게시글 평균 페이지 머문 시간 조회")
    @Test
    public void getvisitorsstaying_200() throws Exception {
        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email1);


        // when & then
        mockMvc.perform(get("/api/post-logs/{post-id}/staying", post.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.errorResponse").value(nullValue()))
                .andExpect(jsonPath("$.content[0].staying").value(375L))
                .andExpect(jsonPath("$.content[0].period").value("202309"))
                .andExpect(jsonPath("$.content[1].staying").value(750L))
                .andExpect(jsonPath("$.content[1].period").value("202403"))
                .andExpect(jsonPath("$.content[2].staying").value(20L))
                .andExpect(jsonPath("$.content[2].period").value("202405"))
                .andExpect(jsonPath("$.content[3].staying").value(380L))
                .andExpect(jsonPath("$.content[3].period").value("total"))
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
                                        .type(JsonFieldType.ARRAY)
                                        .description("응답 내용"),
                                fieldWithPath("content[].staying")
                                        .type(JsonFieldType.NUMBER)
                                        .description("월별 페이지 머문 평균 시간(%)"),
                                fieldWithPath("content[].period")
                                        .type(JsonFieldType.STRING)
                                        .description("해당 년월(전체는 total)"),
                                fieldWithPath("errorResponse")
                                        .type(JsonFieldType.NULL)
                                        .description("에러 응답, 에러가 없는 경우 NULL")
                        )
                ));


    }

    @DisplayName("댓글 작성, 좋아요 클릭 해제 HISTORY 조회")
    @Test
    public void gethistory_200() throws Exception {
        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email1);


        // when & then
        mockMvc.perform(get("/api/post-logs/{post-id}/history",post.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.errorResponse").value(nullValue()))
                .andExpect(jsonPath("$.content[0].memberId").value(1L))
                .andExpect(jsonPath("$.content[0].nickName").value("박창우"))
                .andExpect(jsonPath("$.content[0].profileImage").value("https://ever-trip-bucket.s3.ap-northeast-2.amazonaws.com/basic_user_image.jpg"))
                .andExpect(jsonPath("$.content[0].eventType").value("HISTORY"))
                .andExpect(jsonPath("$.content[0].eventContent").value("CREATE_LIKE"))
                .andExpect(jsonPath("$.content[0].eventCreatedAt").value("2024/06/02 13:14:00"))
                .andExpect(jsonPath("$.content[5].memberId").value(2L))
                .andExpect(jsonPath("$.content[5].nickName").value("김강욱"))
                .andExpect(jsonPath("$.content[5].profileImage").value("https://ever-trip-bucket.s3.ap-northeast-2.amazonaws.com/stock-696x365.jpg_7f156777-707d-4763-8531-dbb9bfe9c4bc"))
                .andExpect(jsonPath("$.content[5].eventType").value("HISTORY"))
                .andExpect(jsonPath("$.content[5].eventContent").value("CREATE_LIKE"))
                .andExpect(jsonPath("$.content[5].eventCreatedAt").value("2024/04/02 13:14:00"))
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
                                        .type(JsonFieldType.ARRAY)
                                        .description("응답 내용"),
                                fieldWithPath("content[].memberId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("멤버 고유 번호"),
                                fieldWithPath("content[].nickName")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 닉네임"),
                                fieldWithPath("content[].profileImage")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 프로필 이미지 주소"),
                                fieldWithPath("content[].eventType")
                                        .type(JsonFieldType.STRING)
                                        .description("발생된 이벤트 타입(VIEWER,SCROLL,STAYING,HISTORY)"),
                                fieldWithPath("content[].eventContent")
                                        .type(JsonFieldType.STRING)
                                        .description("이벤트 내용"),
                                fieldWithPath("content[].eventCreatedAt")
                                        .type(JsonFieldType.STRING)
                                        .description("이벤트 발생 날짜"),
                                fieldWithPath("errorResponse")
                                        .type(JsonFieldType.NULL)
                                        .description("에러 응답, 에러가 없는 경우 NULL")
                        )
                ));


    }

}