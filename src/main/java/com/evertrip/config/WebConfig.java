package com.evertrip.config;

import com.evertrip.post.filter.PostLogFilter;
import com.evertrip.post.repository.PostRepository;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class WebConfig {

    private final PostRepository postRepository;

    @Bean
    public FilterRegistrationBean postLogFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new
                FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new PostLogFilter(postRepository));
        filterRegistrationBean.setOrder(SecurityProperties.DEFAULT_FILTER_ORDER+1);
        filterRegistrationBean.addUrlPatterns("/api/post-logs/*");
        return filterRegistrationBean;
    }
}
