package com.silenthelp.silenthelp.controller;

import com.silenthelp.silenthelp.dto.RegistrationForm;
import com.silenthelp.silenthelp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final UserService userService;
    private final com.silenthelp.silenthelp.service.NotificationService notificationService;

    public AuthController(UserService userService, com.silenthelp.silenthelp.service.NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String username, @RequestParam String email, Model model) {
        String token = userService.createPasswordResetToken(username, email);
        model.addAttribute("successMessage", "If the account exists, a password reset link has been generated.");
        if (token != null) {
            model.addAttribute("resetLink", "/reset-password?token=" + token);
        }
        return "auth/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                @RequestParam String password,
                                @RequestParam String confirmPassword,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("token", token);
            model.addAttribute("error", "Passwords do not match.");
            return "auth/reset-password";
        }
        if (password.length() < 6) {
            model.addAttribute("token", token);
            model.addAttribute("error", "Password must be at least 6 characters.");
            return "auth/reset-password";
        }
        try {
            userService.resetPassword(token, password);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("token", token);
            model.addAttribute("error", ex.getMessage());
            return "auth/reset-password";
        }
        redirectAttributes.addFlashAttribute("success", "Password reset successful. Please sign in.");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token, RedirectAttributes redirectAttributes) {
        try {
            userService.verifyEmail(token);
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/login";
        }
        redirectAttributes.addFlashAttribute("success", "Email verified. Please sign in and accept the platform policies to continue.");
        return "redirect:/login";
    }

    @GetMapping("/verify-email/request")
    public String requestEmailVerification() {
        return "auth/verify-email-request";
    }

    @PostMapping("/verify-email/request")
    public String requestEmailVerification(@RequestParam String account,
                                           Model model) {
        try {
            String token = userService.createEmailVerificationTokenForAccount(account);
            model.addAttribute("verificationLink", "/verify-email?token=" + token);
            model.addAttribute("successMessage", "Verification link generated for this local setup.");
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "auth/verify-email-request";
    }

    @GetMapping("/reactivation-request")
    public String reactivationRequest() {
        return "auth/reactivation-request";
    }

    @PostMapping("/reactivation-request")
    public String reactivationRequest(@RequestParam String account,
                                      @RequestParam String reason,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        try {
            var user = userService.requestReactivation(account, reason);
            notificationService.notifyAdmins("Reactivation request",
                    user.getDisplayName() + " requested account reactivation.",
                    "/admin/deleted-accounts");
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "auth/reactivation-request";
        }
        redirectAttributes.addFlashAttribute("success", "Reactivation request sent. An admin will review it before access is restored.");
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegistrationForm registrationForm,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        try {
            String token = userService.registerStudent(registrationForm);
            redirectAttributes.addFlashAttribute("success", "Account created. Verify your email before signing in.");
            redirectAttributes.addFlashAttribute("verificationLink", "/verify-email?token=" + token);
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("registration.failed", ex.getMessage());
            return "auth/register";
        }
        return "redirect:/login";
    }
}
