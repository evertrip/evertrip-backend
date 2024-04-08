package com.evertrip.oauth.domain;

import org.springframework.http.ResponseEntity;

public interface SocialOauth {

    /**
     * 각 소셜 로그인 페이지로 redirect할 URL build
     * 사용자로부터 로그인 요청을 받은 후 소셜 로그인 서버의 인증용 코드를 보낼 곳
     */
    String getOauthRedirectURL();

    ResponseEntity<String> requestAccessToken(String code);
}