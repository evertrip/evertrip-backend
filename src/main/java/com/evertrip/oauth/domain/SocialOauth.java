package com.evertrip.oauth.domain;

import org.springframework.http.ResponseEntity;

public interface SocialOauth {


    ResponseEntity<String> requestAccessToken(String code);
}