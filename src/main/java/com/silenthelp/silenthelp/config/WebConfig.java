package com.silenthelp.silenthelp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final PolicyAcceptanceInterceptor policyAcceptanceInterceptor;

    public WebConfig(PolicyAcceptanceInterceptor policyAcceptanceInterceptor) {
        this.policyAcceptanceInterceptor = policyAcceptanceInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(policyAcceptanceInterceptor)
                .excludePathPatterns(
                        "/policies/accept",
                        "/logout",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/uploads/**",
                        "/webjars/**",
                        "/error"
                );
    }
}
