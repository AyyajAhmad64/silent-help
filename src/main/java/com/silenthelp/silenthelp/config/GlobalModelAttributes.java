package com.silenthelp.silenthelp.config;

import com.silenthelp.silenthelp.model.User;
import com.silenthelp.silenthelp.service.NotificationService;
import com.silenthelp.silenthelp.service.ConversationService;
import com.silenthelp.silenthelp.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {
    private final UserService userService;
    private final NotificationService notificationService;
    private final ConversationService conversationService;

    public GlobalModelAttributes(UserService userService, NotificationService notificationService,
                                 ConversationService conversationService) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.conversationService = conversationService;
    }

    @ModelAttribute("navUser")
    public User navUser() {
        Authentication authentication = currentAuthentication();
        if (authentication == null) {
            return null;
        }
        return userService.requireByUsername(authentication.getName());
    }

    @ModelAttribute("navUnreadCount")
    public long navUnreadCount() {
        Authentication authentication = currentAuthentication();
        if (authentication == null) {
            return 0;
        }
        return notificationService.unreadCount(userService.requireByUsername(authentication.getName()));
    }

    @ModelAttribute("navChatUnreadCount")
    public long navChatUnreadCount() {
        Authentication authentication = currentAuthentication();
        if (authentication == null) {
            return 0;
        }
        return conversationService.unreadCount(userService.requireByUsername(authentication.getName()));
    }

    private Authentication currentAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return null;
        }
        return authentication;
    }
}
