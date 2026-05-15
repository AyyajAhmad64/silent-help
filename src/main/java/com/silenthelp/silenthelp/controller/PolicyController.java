package com.silenthelp.silenthelp.controller;

import com.silenthelp.silenthelp.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PolicyController {
    private final UserService userService;

    public PolicyController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/policies/accept")
    public String acceptPolicies(Model model) {
        model.addAttribute("policyVersion", userService.currentPolicyVersion());
        return "auth/policy-acceptance";
    }

    @PostMapping("/policies/accept")
    public String acceptPolicies(@RequestParam(required = false) String agree,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        if (!"on".equals(agree)) {
            redirectAttributes.addFlashAttribute("error", "Please review and accept the platform policies to continue.");
            return "redirect:/policies/accept";
        }
        userService.acceptCurrentPolicies(authentication.getName());
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        return "redirect:" + (admin ? "/admin" : "/dashboard");
    }
}
