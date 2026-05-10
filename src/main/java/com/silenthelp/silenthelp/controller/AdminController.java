package com.silenthelp.silenthelp.controller;

import com.silenthelp.silenthelp.model.Category;
import com.silenthelp.silenthelp.model.ReportStatus;
import com.silenthelp.silenthelp.model.RequestStatus;
import com.silenthelp.silenthelp.service.CategoryService;
import com.silenthelp.silenthelp.service.HelpRequestService;
import com.silenthelp.silenthelp.service.NotificationService;
import com.silenthelp.silenthelp.service.ReportService;
import com.silenthelp.silenthelp.service.ResponseService;
import com.silenthelp.silenthelp.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {
    private final UserService userService;
    private final HelpRequestService helpRequestService;
    private final ResponseService responseService;
    private final ReportService reportService;
    private final CategoryService categoryService;
    private final NotificationService notificationService;

    public AdminController(UserService userService, HelpRequestService helpRequestService,
                           ResponseService responseService, ReportService reportService,
                           CategoryService categoryService, NotificationService notificationService) {
        this.userService = userService;
        this.helpRequestService = helpRequestService;
        this.responseService = responseService;
        this.reportService = reportService;
        this.categoryService = categoryService;
        this.notificationService = notificationService;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("requestCount", helpRequestService.visibleCount());
        model.addAttribute("responseCount", responseService.visibleCount());
        model.addAttribute("resolvedCount", helpRequestService.resolvedCount());
        model.addAttribute("openCount", helpRequestService.statusCount(RequestStatus.OPEN));
        model.addAttribute("inProgressCount", helpRequestService.statusCount(RequestStatus.IN_PROGRESS));
        model.addAttribute("openReports", reportService.openCount());
        model.addAttribute("deletedAccountCount", userService.deletedStudentCount());
        return "admin/dashboard";
    }

    @GetMapping("/admin/users")
    public String users(@RequestParam(required = false) String q,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {
        model.addAttribute("users", userService.searchUsers(q, PageRequest.of(Math.max(page, 0), 12)));
        model.addAttribute("query", q);
        return "admin/users";
    }

    @PostMapping("/admin/users/{id}/enabled")
    public String setEnabled(@PathVariable Long id, @RequestParam boolean enabled, RedirectAttributes redirectAttributes) {
        userService.setEnabled(id, enabled);
        redirectAttributes.addFlashAttribute("success", "User status updated.");
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/deleted-accounts")
    public String deletedAccounts(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("users", userService.deletedUsers(PageRequest.of(Math.max(page, 0), 12)));
        return "admin/deleted-accounts";
    }

    @PostMapping("/admin/deleted-accounts/{id}/reactivate")
    public String reactivateDeletedAccount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var user = userService.reactivateDeletedAccount(id);
        notificationService.notify(user, "Account access restored",
                "Admin restored access to your Silent Help account after reviewing your deletion request.",
                "/dashboard");
        redirectAttributes.addFlashAttribute("success", "Account access restored for this student.");
        return "redirect:/admin/deleted-accounts";
    }

    @GetMapping("/admin/posts")
    public String posts(@RequestParam(required = false) String q,
                        @RequestParam(required = false) String category,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {
        model.addAttribute("requests", helpRequestService.browseForAdmin(category, q,
                PageRequest.of(Math.max(page, 0), 12, Sort.by(Sort.Direction.DESC, "createdAt"))));
        model.addAttribute("categories", categoryService.activeCategories());
        model.addAttribute("query", q);
        model.addAttribute("selectedCategory", category);
        return "admin/posts";
    }

    @PostMapping("/admin/posts/{id}/hide")
    public String hidePost(@PathVariable Long id, @RequestParam boolean hidden, RedirectAttributes redirectAttributes) {
        helpRequestService.hide(id, hidden);
        redirectAttributes.addFlashAttribute("success", "Post moderation status updated.");
        return "redirect:/admin/posts";
    }

    @GetMapping("/admin/categories")
    public String categories(Model model) {
        model.addAttribute("categories", categoryService.allCategories());
        model.addAttribute("category", new Category());
        return "admin/categories";
    }

    @PostMapping("/admin/categories")
    public String saveCategory(Category category, RedirectAttributes redirectAttributes) {
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("success", "Category saved.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/admin/categories/{id}/toggle")
    public String toggleCategory(@PathVariable Long id) {
        categoryService.toggle(id);
        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/reports")
    public String reports(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("reports", reportService.all(PageRequest.of(Math.max(page, 0), 15)));
        model.addAttribute("statuses", ReportStatus.values());
        return "admin/reports";
    }

    @PostMapping("/admin/reports/{id}/status")
    public String updateReport(@PathVariable Long id, @RequestParam ReportStatus status, RedirectAttributes redirectAttributes) {
        reportService.updateStatus(id, status);
        redirectAttributes.addFlashAttribute("success", "Report status updated.");
        return "redirect:/admin/reports";
    }
}
