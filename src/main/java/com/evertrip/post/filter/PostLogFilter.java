package com.evertrip.post.filter;

import com.evertrip.api.exception.ApplicationException;
import com.evertrip.api.exception.ErrorCode;
import com.evertrip.api.exception.ErrorResponse;
import com.evertrip.api.response.ApiResponse;
import com.evertrip.constant.ConstantPool;
import com.evertrip.post.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class PostLogFilter implements Filter {

    private PostRepository postRepository;

    public PostLogFilter(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("PostLogFilter init");
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return;
        } else {
            Long memberId = Long.parseLong(authentication.getName());

            String requestURI = httpRequest.getRequestURI();

            Pattern pattern = Pattern.compile("/api/post-logs/(\\d+)/(\\w+)");
            Matcher matcher = pattern.matcher(requestURI);

            try {
                if (matcher.find()) {
                    Long postId = Long.parseLong(matcher.group(1));
                    postRepository.getPostByPostIdAndMemberId(memberId, postId).orElseThrow(() -> new ApplicationException(ErrorCode.NOT_WRITER));
                    chain.doFilter(request,response);
                } else {
                    log.info("알맞지 않은 URL 요청", requestURI);
                    throw new ApplicationException(ErrorCode.URL_NOT_FOUND);
                }
            } catch (ApplicationException e) {
                sendErrorResponse(httpResponse, e);
                return;
            }



        }
    }

    private void sendErrorResponse(HttpServletResponse response, ApplicationException e) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(ApiResponse.error(ErrorResponse.of(e.getErrorCode())));

        response.setContentType("application/json");
        response.setStatus(e.getErrorCode().getStatus().value());
        response.getWriter().write(jsonInString);
    }

    @Override
    public void destroy() {
        log.info("PostLogFilter destroy");
        Filter.super.destroy();
    }

}
