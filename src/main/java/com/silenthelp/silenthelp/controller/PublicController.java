package com.silenthelp.silenthelp.controller;

import com.silenthelp.silenthelp.service.CategoryService;
import com.silenthelp.silenthelp.service.HelpRequestService;
import com.silenthelp.silenthelp.service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PublicController {
    private final CategoryService categoryService;
    private final HelpRequestService helpRequestService;
    private final NotificationService notificationService;

    public PublicController(CategoryService categoryService, HelpRequestService helpRequestService,
                            NotificationService notificationService) {
        this.categoryService = categoryService;
        this.helpRequestService = helpRequestService;
        this.notificationService = notificationService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("categories", categoryService.activeCategories());
        model.addAttribute("latestRequests", helpRequestService.latest());
        return "public/home";
    }

    @GetMapping("/about")
    public String about() {
        return "public/about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "public/contact";
    }

    @PostMapping("/contact")
    public String submitContact(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam String message,
                                RedirectAttributes redirectAttributes) {
        notificationService.notifyAdmins("New contact message",
                trimForNotification(name.trim() + " (" + email.trim() + "): " + message.trim()),
                "/notifications");
        redirectAttributes.addFlashAttribute("success", "Thanks, " + name.trim() + ". Your message has been received.");
        return "redirect:/contact";
    }

    @GetMapping("/feedback")
    public String feedback() {
        return "public/feedback";
    }

    @GetMapping("/community-guidelines")
    public String communityGuidelines() {
        return "public/community-guidelines";
    }

    @GetMapping("/terms")
    public String terms() {
        return "public/terms";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "public/privacy";
    }

    @GetMapping("/report-issue")
    public String reportIssue() {
        return "public/report-issue";
    }

    @PostMapping("/report-issue")
    public String submitIssue(@RequestParam String name,
                              @RequestParam String email,
                              @RequestParam String issue,
                              RedirectAttributes redirectAttributes) {
        notificationService.notifyAdmins("Issue reported",
                trimForNotification(name.trim() + " (" + email.trim() + "): " + issue.trim()),
                "/notifications");
        redirectAttributes.addFlashAttribute("success", "Issue submitted. The moderation team will review it.");
        return "redirect:/report-issue";
    }

    @PostMapping("/feedback")
    public String submitFeedback(@RequestParam String name,
                                 @RequestParam String feedback,
                                 @RequestParam(required = false) String rating,
                                 RedirectAttributes redirectAttributes) {
        String ratingText = rating == null || rating.isBlank() ? "No rating" : rating.trim();
        notificationService.notifyAdmins("New feedback",
                trimForNotification(name.trim() + " - " + ratingText + ": " + feedback.trim()),
                "/notifications");
        redirectAttributes.addFlashAttribute("success", "Thank you for helping improve Silent Help.");
        return "redirect:/feedback";
    }

    private String trimForNotification(String value) {
        return value.length() <= 250 ? value : value.substring(0, 247) + "...";
    }
}
