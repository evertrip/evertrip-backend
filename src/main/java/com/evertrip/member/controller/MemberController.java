package com.evertrip.member.controller;

import com.evertrip.api.exception.ApplicationException;
import com.evertrip.api.exception.ErrorCode;
import com.evertrip.api.response.ApiResponse;
import com.evertrip.member.dto.request.MemberProfilePatchDto;
import com.evertrip.member.dto.response.MemberProfileResponseDto;
import com.evertrip.member.dto.response.MemberSimpleResponseDto;
import com.evertrip.member.service.MemberService;
import com.evertrip.security.jwt.HmacAndBase64;
import com.evertrip.security.redis.TokenStorageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final TokenStorageService redisService;

    private final HmacAndBase64 hmacAndBase64;

    private final MemberService memberService;

    /**
     * 로그아웃 시 토큰 제거
     */
    @GetMapping("/removeToken")
    public ResponseEntity removeToken(Principal principal, HttpServletRequest request) {
        try {
            // Refresh 토큰을 Redis에서 제거하는 작업
            redisService.removeRefreshToken("refresh:" + hmacAndBase64.crypt(request.getRemoteAddr(), "HmacSHA512") + "_" + principal.getName());
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new ApplicationException(ErrorCode.CRYPT_ERROR);
        }

        return new ResponseEntity(ApiResponse.successOf(principal.getName() + " 삭제완료"), HttpStatus.OK);
    }

    /**
     * 개인 회원정보 조회
     */
    @GetMapping("/members")
    public ResponseEntity getMember(Principal principal) {
        Long memberId = Long.parseLong(principal.getName());
        List<MemberProfileResponseDto> memberProfiles = memberService.getMemberProfiles(memberId);
        log.info("{}",memberProfiles);
        return new ResponseEntity(ApiResponse.successOf(memberProfiles), HttpStatus.OK);
    }

    /**
     * 회원 프로필 수정
     */
    @PatchMapping("/members/profile")
    public ResponseEntity updateMemberProfile(@Valid @RequestBody MemberProfilePatchDto dto, BindingResult bindingResult, Principal principal) {
        // Validation 체크
        if (bindingResult.hasErrors()) {

            if (bindingResult.hasFieldErrors("nickName")) {
                throw new ApplicationException(ErrorCode.INCORRECT_FORMAT_NICKNAME);
            }

            if (bindingResult.hasFieldErrors("description")) {
                throw new ApplicationException(ErrorCode.INCORRECT_FORMAT_DESCRIPTION);
            }

        }

        Long memberId = Long.parseLong(principal.getName());
        memberService.updateMemberProfile(memberId, dto);
        return new ResponseEntity<>(ApiResponse.successOf(new MemberSimpleResponseDto(memberId)), HttpStatus.OK);
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/members")
    public ResponseEntity updateMemberDelete(Principal principal, HttpServletRequest request) {
        Long memberId = Long.parseLong(principal.getName());
        memberService.updateMemberDelete(memberId, request);

        // Todo: Access Token은 요청을 보낸 Axios API에서 쿠키를 삭제하도록 지시한다.
        return new ResponseEntity(ApiResponse.successOf(new MemberSimpleResponseDto(memberId)), HttpStatus.OK);
    }
}
