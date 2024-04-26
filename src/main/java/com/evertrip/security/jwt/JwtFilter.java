package com.evertrip.security.jwt;

import com.evertrip.api.exception.ApplicationException;
import com.evertrip.security.exception.ExpiredTokenException;
import com.evertrip.security.exception.NoJwtTokenException;
import com.evertrip.security.redis.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static com.evertrip.constant.ConstantPool.AUTHORIZATION_HEADER;
import static com.evertrip.constant.ConstantPool.REFRESH_HEADER;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends GenericFilterBean {

    private final TokenProvider tokenProvider;

    private final RefreshTokenProvider refreshTokenProvider;

    private final RedisService redisService;

    private final HmacAndBase64 hmacAndBase64;

    // doFilter는 토큰의 인증정보를 SecurityContext에 저장하는 역할을 수행한다.
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        // resolveToken을 통해 토큰을 받아와서 유효성 검증을 하고 정상 토큰이면 SecurityContext에 저장한다.
        String jwt = resolveToken(httpServletRequest);
        String refreshToken = resolveRefreshToken(httpServletRequest);

        String requestURI = httpServletRequest.getRequestURI();
        String ipAddress = httpServletRequest.getRemoteAddr();

        try {
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                Authentication authentication = tokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
            } else {
                log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
                throw new NoJwtTokenException("JWT 토큰이 없습니다. Refresh 토큰을 확인하겠습니다.");
            }
        } catch (ExpiredTokenException | NoJwtTokenException e) {

            try {
                if (StringUtils.hasText(refreshToken) && refreshTokenProvider.validateToken(refreshToken, ipAddress)) {
                    Authentication authentication = tokenProvider.getAuthentication(refreshToken);

                    // Redis에 저장된 refresh 토큰과 비교
                    if (redisService.getRefreshToken("refresh:"+
                            hmacAndBase64.crypt(ipAddress,"HmacSHA512")+"_"+authentication.getName()).equals(refreshToken)) {


                        String renewToken = tokenProvider.createToken(authentication);

                        httpServletResponse.setHeader(AUTHORIZATION_HEADER,"Bearer " + renewToken);
                    }


                } else {
                    log.debug("유효한 Refresh 토큰이 없습니다, uri: {}", requestURI);
                }


            } catch (ExpiredTokenException ete) {
                log.warn("Refresh 토큰의 만료기간이 지났습니다.");
            } catch (ApplicationException ae) {
                log.warn("Refresh 토큰의 인증이 잘못됐습니다.");
            } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException niu) {
                log.warn("ipAddress 암호화 실패");
            }
        } catch (ApplicationException e) {
            log.warn("{}",e);
            log.warn("JWT 토큰 인증에 실패하였습니다.");
        }


        filterChain.doFilter(servletRequest, servletResponse);
    }

    // Request Header에서 토큰 정보를 꺼내오기 위한 resolveToken 메소드 추가
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        log.debug("헤더의 Access 토큰 : {}", bearerToken);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    // Request Header에서 리프레쉬 토큰 정보를 꺼내오기 위한 resolveRefreshToken 메소드 추가
    private String resolveRefreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader(REFRESH_HEADER);


        log.debug("헤더의 Refresh 토큰 : {}", refreshToken);

        if (StringUtils.hasText(refreshToken) && refreshToken.startsWith("Bearer ")) {
            return refreshToken.substring(7);
        }

        return null;
    }
}
