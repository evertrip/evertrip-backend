package com.evertrip.constant;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.List;

public class ConstantPool {

    // HEADER
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_HEADER = "Refresh";



    // page navigates(페이지 쪽수의 갯수)
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUM = 0;

    // Redis의 조회수을 읽어오기 위한 패턴
    public static final String viewPattern = "views::*";


    // 프론트 서버 주소
    public static final String FRONT_SERVER_HOST = "FRONT_SERVER_HOST";
    public static final String FRONT_LOCAL_HOST = "http://localhost:3000";


    //  FILE SCHEDULING DURATION
    public static final Integer FILE_EXPIRATION_DURATION = 30*6;


    // SPRING CACHING
    public class CacheName {

        public static final String POST = "post";

        public static final String VIEWS = "views";

        public static final String VIEWERS = "viewers";
    }

    // POST_EVENT_TYPE
    public enum EventType {
        VIEWER,
        SCROLL,
        STAYING,
        HISTORY
    }

    //메인페이지 view, best slide post 개수
    public static final int MAIN_POST_SIZE_FOR_SLIDE = 30;

    // 소셜 로그인 타입
    public enum SocialLoginType {
        GOOGLE,
        NAVER,
        KAKAO
    }


    // 이미지 타입
    public static List<String> imageExtList = List.of("jpg", "jpeg", "gif", "png");

}