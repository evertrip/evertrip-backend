package com.evertrip.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class NaverOauthToken {
    private String access_token;
    private int expires_in;
    private String token_type;
    private String refresh_token;
}
