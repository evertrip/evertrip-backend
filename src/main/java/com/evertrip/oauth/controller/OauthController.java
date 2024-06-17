package com.evertrip.oauth.controller;

import com.evertrip.api.response.ApiResponse;
import com.evertrip.constant.ConstantPool;
import com.evertrip.constant.ConstantPool.SocialLoginType;
import com.evertrip.member.dto.response.MemberSimpleResponseDto;
import com.evertrip.oauth.service.OauthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OauthController {

    private final OauthService oauthService;


    /**
     * 소셜 로그인 요청에 대한 응답 처리(로그인, 회원가입 처리)
     */
    @GetMapping("/auth/{socialLoginType}/callback")
    public ResponseEntity callback(
            @PathVariable(name="socialLoginType") String type,
            @RequestParam(name="code") String code,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        SocialLoginType socialLoginType= SocialLoginType.valueOf(type.toUpperCase());
        MemberSimpleResponseDto memberSimpleResponseDto = oauthService.oauthLogin(socialLoginType, code, request, response);
        return ResponseEntity.ok(memberSimpleResponseDto);
    }

}

