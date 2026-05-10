package com.silenthelp.silenthelp.controller;

import com.silenthelp.silenthelp.model.User;
import com.silenthelp.silenthelp.service.ConversationService;
import com.silenthelp.silenthelp.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ConversationController {
    private final ConversationService conversationService;
    private final UserService userService;

    public ConversationController(ConversationService conversationService, UserService userService) {
        this.conversationService = conversationService;
        this.userService = userService;
    }

    @GetMapping("/chats")
    public String inbox(Authentication authentication, Model model) {
        User user = currentUser(authentication);
        model.addAttribute("user", user);
        model.addAttribute("conversations", conversationService.forUser(user));
        return "user/chats";
    }

    @PostMapping("/requests/{requestId}/chat")
    public String startWithRequester(@PathVariable Long requestId,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        try {
            var conversation = conversationService.startWithRequester(requestId, currentUser(authentication));
            return "redirect:/chats/" + conversation.getId();
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("success", ex.getMessage());
            return "redirect:/requests/" + requestId;
        }
    }

    @PostMapping("/requests/{requestId}/responses/{responseId}/chat")
    public String start(@PathVariable Long requestId, @PathVariable Long responseId,
                        Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            var conversation = conversationService.startFromResponse(requestId, responseId, currentUser(authentication));
            return "redirect:/chats/" + conversation.getId();
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("success", ex.getMessage());
            return "redirect:/requests/" + requestId;
        }
    }

    @GetMapping("/chats/{id}")
    public String thread(@PathVariable Long id, Authentication authentication, Model model,
                         RedirectAttributes redirectAttributes) {
        try {
            User user = currentUser(authentication);
            model.addAttribute("user", user);
            model.addAttribute("conversation", conversationService.accessible(id, user));
            return "user/chat-thread";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("success", ex.getMessage());
            return "redirect:/chats";
        }
    }

    @PostMapping("/chats/{id}/messages")
    public String send(@PathVariable Long id, @RequestParam String message,
                       Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            conversationService.send(id, message, currentUser(authentication));
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("success", ex.getMessage());
        }
        return "redirect:/chats/" + id;
    }

    private User currentUser(Authentication authentication) {
        return userService.requireByUsername(authentication.getName());
    }
}
