package com.silenthelp.silenthelp.config;

import com.silenthelp.silenthelp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class PolicyAcceptanceInterceptor implements HandlerInterceptor {
    private final UserService userService;

    public PolicyAcceptanceInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return true;
        }
        if (userService.needsPolicyAcceptance(authentication.getName())) {
            response.sendRedirect("/policies/accept");
            return false;
        }
        return true;
    }
}
