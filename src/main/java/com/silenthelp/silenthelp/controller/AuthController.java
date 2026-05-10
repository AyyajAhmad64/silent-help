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

    public AuthController(UserService userService) {
        this.userService = userService;
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
            userService.registerStudent(registrationForm);
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("registration.failed", ex.getMessage());
            return "auth/register";
        }
        redirectAttributes.addFlashAttribute("success", "Account created. Please sign in to continue.");
        return "redirect:/login";
    }
}
