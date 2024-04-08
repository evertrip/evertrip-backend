package com.evertrip.oauth.domain;

import com.evertrip.oauth.dto.NaverOauthToken;
import com.evertrip.oauth.dto.NaverUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NaverOauth implements SocialOauth{

    @Value("${spring.security.oauth2.naver.authorization}")
    private String NAVER_SNS_LOGIN_URL;

    @Value("${spring.security.oauth2.naver.client-id}")
    private String NAVER_SNS_CLIENT_ID;

    @Value("${spring.security.oauth2.naver.redirect-url}")
    private String NAVER_SNS_CALLBACK_URL;

    @Value("${spring.security.oauth2.naver.client-secret}")
    private String NAVER_SNS_CLIENT_SECRET;

    @Value("${spring.security.oauth2.naver.scope}")
    private String NAVER_DATA_ACCESS_SCOPE;

    @Value("${spring.security.oauth2.naver.token-uri}")
    private String NAVER_TOKEN_URI;

    @Value("${spring.security.oauth2.naver.user-info-uri}")
    private String NAVER_USER_INFO_URI;

    @Value("${spring.security.oauth2.naver.authorization-grant-type}")
    private String NAVER_AUTHORIZATION_GRANT_TYPE;

    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate;



    @Override
    public String getOauthRedirectURL() {

        Map<String,Object> params=new HashMap<>();
        params.put("scope",NAVER_DATA_ACCESS_SCOPE);
        params.put("response_type","code");
        params.put("client_id",NAVER_SNS_CLIENT_ID);
        params.put("redirect_uri",NAVER_SNS_CALLBACK_URL);

        //parameter를 형식에 맞춰 구성해주는 함수
        String parameterString=params.entrySet().stream()
                .map(x->x.getKey()+"="+x.getValue())
                .collect(Collectors.joining("&"));
        String redirectURL=NAVER_SNS_LOGIN_URL+"?"+parameterString;
        System.out.println("redirectURL = " + redirectURL);

        return redirectURL;
    }

    @Override
    public ResponseEntity<String> requestAccessToken(String code) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", NAVER_AUTHORIZATION_GRANT_TYPE);
        params.add("client_id", NAVER_SNS_CLIENT_ID);
        params.add("client_secret", NAVER_SNS_CLIENT_SECRET);
        params.add("code", code);


        System.out.println("params :" + params.toString());

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // HttpEntity 객체 생성
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);


        ResponseEntity<String> responseEntity=restTemplate.postForEntity(NAVER_TOKEN_URI,
                requestEntity,String.class);

        if(responseEntity.getStatusCode()== HttpStatus.OK){
            return responseEntity;
        }
        return null;

    }

    public NaverOauthToken getAccessToken(ResponseEntity<String> response) throws JsonProcessingException {
        System.out.println("response.getBody() = " + response.getBody());
        NaverOauthToken naverOauthToken= objectMapper.readValue(response.getBody(), NaverOauthToken.class);
        return naverOauthToken;
    }

    public ResponseEntity<String> requestUserInfo(NaverOauthToken oauthToken) {

        //header에 accessToken을 담는다.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer "+oauthToken.getAccess_token());

        //HttpEntity를 하나 생성해 헤더를 담아서 restTemplate으로 서드파티(네이버)와 통신하게 된다.
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
        ResponseEntity<String> response=restTemplate.exchange(NAVER_USER_INFO_URI, HttpMethod.GET,request,String.class);
        System.out.println("responseUserInfo.getBody() = " + response.getBody());
        return response;
    }

    public NaverUser getUserInfo(ResponseEntity<String> userInfoRes) throws JsonProcessingException{

        // 응답 결과에서 response에 해당되는 내용을 파싱하는 과정
        JsonNode rootNode = objectMapper.readTree(userInfoRes.getBody());
        JsonNode responseNode = rootNode.path("response");
        NaverUser naverUser= objectMapper.treeToValue(responseNode, NaverUser.class);
        System.out.println("NaverUser : "+naverUser);
        return naverUser;
    }


}
