package com.evertrip.oauth.controller;

import com.evertrip.api.response.ApiResponse;
import com.evertrip.constant.ConstantPool;
import com.evertrip.constant.ConstantPool.SocialLoginType;
import com.evertrip.member.dto.response.MemberSimpleResponseDto;
import com.evertrip.oauth.service.OauthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class OauthController {

    private final OauthService oauthService;

    /**
     * 소셜 로그인 요청
     */
    @GetMapping("/auth/{socialLoginType}")
    public void socialLoginRedirect(@PathVariable(name="socialLoginType") String type)  throws IOException {
        SocialLoginType socialLoginType= SocialLoginType.valueOf(type.toUpperCase());
        oauthService.request(socialLoginType);
    }

    /**
     * 소셜 로그인 요청에 대한 응답 처리(로그인, 회원가입 처리)
     */
    @GetMapping("/auth/{socialLoginType}/callback")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<MemberSimpleResponseDto> callback(
            @PathVariable(name="socialLoginType") String type,
            @RequestParam(name="code") String code,
            HttpServletRequest request) throws IOException {

        System.out.println(">> 소셜 로그인 API 서버로부터 받은 code :"+ code);
        HttpHeaders httpHeaders = new HttpHeaders();
        SocialLoginType socialLoginType= SocialLoginType.valueOf(type.toUpperCase());
        Long memberId=Long.parseLong(oauthService.oauthLogin(socialLoginType,code,httpHeaders,request));
        return ApiResponse.successOf(new MemberSimpleResponseDto(memberId));
    }

}

