package com.evertrip.oauth.service;

import com.evertrip.api.exception.ApplicationException;
import com.evertrip.api.exception.ErrorCode;
import com.evertrip.constant.ConstantPool;
import com.evertrip.member.entity.Member;
import com.evertrip.member.entity.MemberDetail;
import com.evertrip.member.entity.MemberProfile;
import com.evertrip.member.entity.Role;
import com.evertrip.member.repository.MemberDetailRepository;
import com.evertrip.member.repository.MemberProfileRepository;
import com.evertrip.member.repository.MemberRepository;
import com.evertrip.member.repository.RoleRepository;
import com.evertrip.oauth.domain.NaverOauth;
import com.evertrip.oauth.dto.NaverOauthToken;
import com.evertrip.oauth.dto.NaverUser;
import com.evertrip.security.jwt.HmacAndBase64;
import com.evertrip.security.jwt.RefreshTokenProvider;
import com.evertrip.security.jwt.SymmetricCrypto;
import com.evertrip.security.jwt.TokenProvider;
import com.evertrip.security.redis.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static com.evertrip.api.exception.ErrorCode.INVALID_SOCIAL_LOGIN_TYPE;
import static com.evertrip.constant.ConstantPool.AUTHORIZATION_HEADER;
import static com.evertrip.constant.ConstantPool.REFRESH_HEADER;

@Service
@RequiredArgsConstructor
@Slf4j
public class OauthService {
    private final NaverOauth naverOauth;
    private final HttpServletResponse response;

    private final MemberRepository memberRepository;

    private final MemberProfileRepository memberProfileRepository;

    private final MemberDetailRepository memberDetailRepository;

    private final RedisService redisService;

    private final SymmetricCrypto symmetricCrypto;

    private final HmacAndBase64 hmacAndBase64;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final TokenProvider tokenProvider;

    private final RefreshTokenProvider refreshTokenProvider;

    private final RoleRepository roleRepository;



    public void request(ConstantPool.SocialLoginType socialLoginType) throws IOException {
        String redirectURL = switch (socialLoginType) {
            case NAVER -> {
                yield naverOauth.getOauthRedirectURL();
            }
            default -> {
                throw new ApplicationException(INVALID_SOCIAL_LOGIN_TYPE);
            }
        };

        response.sendRedirect(redirectURL);
    }

    @Transactional
    public String oauthLogin(ConstantPool.SocialLoginType socialLoginType, String code, HttpHeaders httpHeaders , HttpServletRequest request) throws IOException {
        switch (socialLoginType) {
            case NAVER -> {
                // 네이버로 일회성 코드를 보내 액세스 토큰이 담긴 응답객체를 받아온다
                ResponseEntity<String> accessTokenResponse = naverOauth.requestAccessToken(code);
                // 응답 객체가 JSON 형식으로 되어 있으므로, 이를 역직렬화해서 자바 객체에 담는다.
                NaverOauthToken oauthToken = naverOauth.getAccessToken(accessTokenResponse);
                // 액세스 토큰을 다시 네이버로 보내 네이버에 저장된 사용자 정보가 담긴 응답 객체를 받아온다.
                ResponseEntity<String> userInfoResponse = naverOauth.requestUserInfo(oauthToken);
                // 다시 JSON 형식의 응답 객체를 자바 객체로 역직렬화한다.
                NaverUser naverUser = naverOauth.getUserInfo(userInfoResponse);

                // DB에 회원 이메일이 등록 되어있는지 확인 후 있으면 회원가입 처리 및 토큰 발급, 없으면 그냥 토큰 발급
                // 회원가입 처리
                if (memberRepository.findByEmail(symmetricCrypto.encrypt(naverUser.getEmail())).isEmpty()) {
                    log.info("신규 회원");
                    Role role = roleRepository.findByRoleName(Role.RoleName.USER).orElseThrow(() -> new ApplicationException(ErrorCode.AUTHORITY_NOT_FOUND));
                    Member member = new Member(symmetricCrypto.encrypt(naverUser.getEmail()),role);
                    Member findMember = memberRepository.save(member);

                    MemberProfile memberProfile = new MemberProfile(member, naverUser.getName());
                    memberProfileRepository.save(memberProfile);

                    MemberDetail memberDetail = new MemberDetail(member,naverUser.getName(), symmetricCrypto.encrypt(naverUser.getMobile()), naverUser.getGender(), naverUser.getAge());
                    memberDetailRepository.save(memberDetail);



                    log.info("회원 비교 : {}", (member==findMember));
                    log.info("회원 이메일(실제) : {}",naverUser.getEmail());
                    log.info("회원 이메일(DB 암호화) : {}" ,findMember.getEmail());
                    log.info("회원 이메일(DB 복호화) : {}" ,symmetricCrypto.decrypt(findMember.getEmail()));
                    log.info("회원 전화번호(DB 암호화) : {}" ,memberDetail.getPhone());
                    log.info("회원 전화번호(DB 복호화) : {}" ,symmetricCrypto.decrypt(memberDetail.getPhone()));
                }

                log.info("해당 회원 저장 여부 : {}",memberRepository.findByEmail(symmetricCrypto.encrypt(naverUser.getEmail())).get().getEmail());

                // JWT 토큰 발급
                // ----------------------------------------------------------
                String ipAddress = request.getRemoteAddr();

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(symmetricCrypto.encrypt(naverUser.getEmail()),null);

                // authenticationToken을 이용해서 Authentication 객체를 생성하려고 authentication 메소드가 실행이 될 때
                // CustomUserDetailsService의 loadUserByUsername 메소드가 실행된다.
                Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

                // createToken 메소드를 통해서 JWT Token 생성
                String jwt = tokenProvider.createToken(authentication);
                String refresh = refreshTokenProvider.createToken(authentication, ipAddress);

                httpHeaders.add(AUTHORIZATION_HEADER, "Bearer " + jwt);
                httpHeaders.add(REFRESH_HEADER, "Bearer " + refresh);

                // REDIS에 Refresh Token 저장
                try {
                    // "refresh:암호화된IP_pk"을 key값으로 refreshToken을 Redis에 저장한다.
                    redisService.setRefreshToken("refresh:" + hmacAndBase64.crypt(ipAddress, "HmacSHA512")
                            + "_" + authentication.getName(), refresh);
                } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
                    throw new ApplicationException(ErrorCode.CRYPT_ERROR);
                }
                // ----------------------------------------------------------

                return authentication.getName();

            }
            default -> {
                throw new ApplicationException(INVALID_SOCIAL_LOGIN_TYPE);
            }
        }
    }
}
