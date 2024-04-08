package com.evertrip.security.jwt;

import com.evertrip.api.exception.ErrorCode;
import com.evertrip.api.exception.ErrorResponse;
import com.evertrip.api.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 필요한 권한이 존재하지 않는 경우에 403 Forbidden 에러를 리턴하기 위한 JwtAccessDeniedHandler 클래스
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(ApiResponse.error(ErrorResponse.of(ErrorCode.ACCESS_FORBIDDEN)));

        response.setContentType("application/json");
        //필요한 권한이 없이 접근하려 할때 403
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(jsonInString);
    }
}
