package com.silenthelp.silenthelp.controller;

import com.silenthelp.silenthelp.model.User;
import com.silenthelp.silenthelp.service.HelpRequestService;
import com.silenthelp.silenthelp.service.NotificationService;
import com.silenthelp.silenthelp.service.ResponseService;
import com.silenthelp.silenthelp.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DashboardController {
    private final UserService userService;
    private final HelpRequestService helpRequestService;
    private final ResponseService responseService;
    private final NotificationService notificationService;

    public DashboardController(UserService userService, HelpRequestService helpRequestService,
                               ResponseService responseService, NotificationService notificationService) {
        this.userService = userService;
        this.helpRequestService = helpRequestService;
        this.responseService = responseService;
        this.notificationService = notificationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User user = currentUser(authentication);
        var requests = helpRequestService.mine(user);
        model.addAttribute("user", user);
        model.addAttribute("requests", requests);
        model.addAttribute("responses", responseService.mine(user));
        model.addAttribute("savedRequests", helpRequestService.savedBy(user));
        model.addAttribute("unread", notificationService.unreadCount(user));
        model.addAttribute("openRequests", requests.stream().filter(request -> !request.isResolved()).count());
        model.addAttribute("resolvedRequests", requests.stream().filter(request -> request.isResolved()).count());
        model.addAttribute("urgentRequests", requests.stream().filter(request -> "URGENT".equals(request.getUrgency()) || "HIGH".equals(request.getUrgency())).count());
        long helpfulVotes = responseService.helpfulVotesFor(user);
        long acceptedAnswers = helpRequestService.acceptedAnswersBy(user);
        model.addAttribute("helpfulVotes", helpfulVotes);
        model.addAttribute("acceptedAnswers", acceptedAnswers);
        model.addAttribute("contributionScore", helpfulVotes * 10 + acceptedAnswers * 25 + responseService.mine(user).size() * 3);
        return "user/dashboard";
    }

    @GetMapping("/my-requests")
    public String myRequests(Authentication authentication, Model model) {
        model.addAttribute("requests", helpRequestService.mine(currentUser(authentication)));
        return "user/my-requests";
    }

    @GetMapping("/my-responses")
    public String myResponses(Authentication authentication, Model model) {
        model.addAttribute("responses", responseService.mine(currentUser(authentication)));
        return "user/my-responses";
    }

    @GetMapping("/notifications")
    public String notifications(Authentication authentication, Model model) {
        model.addAttribute("notifications", notificationService.forUser(currentUser(authentication)));
        return "user/notifications";
    }

    @PostMapping("/notifications/read")
    public String markNotificationRead(@RequestParam Long id, Authentication authentication) {
        notificationService.markRead(id, currentUser(authentication));
        return "redirect:/notifications";
    }

    @GetMapping("/notifications/{id}/open")
    public String openNotification(@PathVariable Long id, Authentication authentication) {
        String link = notificationService.openAndMarkRead(id, currentUser(authentication));
        return "redirect:" + link;
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        User user = currentUser(authentication);
        model.addAttribute("user", user);
        model.addAttribute("isAdmin", user.hasRole(com.silenthelp.silenthelp.model.RoleName.ADMIN));
        return "user/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam String displayName,
                                @RequestParam(required = false) String department,
                                @RequestParam(required = false) String yearOfStudy,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        userService.updateProfile(authentication.getName(), displayName, department, yearOfStudy);
        redirectAttributes.addFlashAttribute("success", "Profile updated.");
        return "redirect:/profile";
    }

    @PostMapping("/account/delete")
    public String deleteAccount(@RequestParam String reason,
                                Authentication authentication,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                RedirectAttributes redirectAttributes) {
        try {
            User deletedUser = userService.deleteOwnAccount(authentication.getName(), reason);
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/profile";
        }
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        return "redirect:/login?account=deleted_success";
    }

    private String trimForNotification(String value) {
        return value.length() <= 250 ? value : value.substring(0, 247) + "...";
    }

    private User currentUser(Authentication authentication) {
        return userService.requireByUsername(authentication.getName());
    }
}
