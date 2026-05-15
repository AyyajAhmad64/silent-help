package com.silenthelp.silenthelp.controller;

import com.silenthelp.silenthelp.dto.HelpRequestForm;
import com.silenthelp.silenthelp.dto.ReportForm;
import com.silenthelp.silenthelp.dto.ResponseForm;
import com.silenthelp.silenthelp.model.HelpRequest;
import com.silenthelp.silenthelp.model.ReportTargetType;
import com.silenthelp.silenthelp.model.RequestStatus;
import com.silenthelp.silenthelp.model.Response;
import com.silenthelp.silenthelp.model.User;
import com.silenthelp.silenthelp.service.CategoryService;
import com.silenthelp.silenthelp.service.FileStorageService;
import com.silenthelp.silenthelp.service.HelpRequestService;
import com.silenthelp.silenthelp.service.ReportService;
import com.silenthelp.silenthelp.service.ResponseService;
import com.silenthelp.silenthelp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Controller
public class HelpRequestController {
    private static final DateTimeFormatter RESPONSE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd MMM, HH:mm");

    private final HelpRequestService helpRequestService;
    private final ResponseService responseService;
    private final ReportService reportService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    public HelpRequestController(HelpRequestService helpRequestService, ResponseService responseService,
                                 ReportService reportService, CategoryService categoryService, UserService userService,
                                 FileStorageService fileStorageService) {
        this.helpRequestService = helpRequestService;
        this.responseService = responseService;
        this.reportService = reportService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/requests")
    public String browse(@RequestParam(required = false) String category,
                         @RequestParam(required = false) String q,
                         @RequestParam(required = false) String urgency,
                         @RequestParam(required = false) String status,
                         @RequestParam(defaultValue = "0") int page,
                         Model model) {
        var pageable = PageRequest.of(Math.max(page, 0), 8, Sort.by(Sort.Direction.DESC, "createdAt"));
        model.addAttribute("requests", helpRequestService.browse(category, q, urgency, status, pageable));
        model.addAttribute("categories", categoryService.activeCategories());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedUrgency", urgency);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("query", q);
        return "requests/browse";
    }

    @GetMapping("/requests/new")
    public String newRequest(Model model) {
        model.addAttribute("helpRequestForm", new HelpRequestForm());
        model.addAttribute("categories", categoryService.activeCategories());
        return "requests/create";
    }

    @PostMapping("/requests")
    public String create(@Valid @ModelAttribute HelpRequestForm helpRequestForm,
                         BindingResult bindingResult,
                         @RequestParam(required = false) MultipartFile attachmentImage,
                         Authentication authentication,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.activeCategories());
            return "requests/create";
        }
        try {
            String uploadedImageUrl = fileStorageService.storeRequestImage(attachmentImage);
            if (uploadedImageUrl != null) {
                helpRequestForm.setAttachmentUrl(uploadedImageUrl);
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            bindingResult.rejectValue("attachmentUrl", "attachment.invalid", ex.getMessage());
            model.addAttribute("categories", categoryService.activeCategories());
            return "requests/create";
        }
        HelpRequest saved = helpRequestService.create(helpRequestForm, currentUser(authentication));
        redirectAttributes.addFlashAttribute("success", saved.isAnonymous()
                ? "Your anonymous help request is live."
                : "Your help request is live with your display name.");
        return "redirect:/requests/" + saved.getId();
    }

    @GetMapping("/requests/{id}")
    public String details(@PathVariable Long id, Authentication authentication, Model model) {
        User viewer = authentication == null ? null : currentUser(authentication);
        populateDetailsModel(id, viewer, model);
        if (!model.containsAttribute("responseForm")) {
            model.addAttribute("responseForm", new ResponseForm());
        }
        return "requests/details";
    }

    @PostMapping("/requests/{id}/responses")
    public String respond(@PathVariable Long id,
                          @Valid @ModelAttribute ResponseForm responseForm,
                          BindingResult bindingResult,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes,
            Model model) {
        User viewer = currentUser(authentication);
        if (bindingResult.hasErrors()) {
            populateDetailsModel(id, viewer, model);
            model.addAttribute("error", "Please write a response between 5 and 3000 characters.");
            return "requests/details";
        }
        try {
            responseService.respond(id, responseForm, viewer);
            redirectAttributes.addFlashAttribute("success", responseForm.isAnonymous()
                    ? "Response posted anonymously."
                    : "Response posted with your display name.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/requests/" + id;
    }

    @PostMapping(value = "/requests/{id}/responses", headers = "X-Requested-With=XMLHttpRequest")
    public ResponseEntity<Map<String, Object>> respondAjax(@PathVariable Long id,
                                                           @Valid @ModelAttribute ResponseForm responseForm,
                                                           BindingResult bindingResult,
                                                           Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "ok", false,
                    "message", "Please write a response between 5 and 3000 characters."
            ));
        }
        try {
            User viewer = currentUser(authentication);
            Response response = responseService.respond(id, responseForm, viewer);
            return ResponseEntity.ok(Map.of(
                    "ok", true,
                    "message", responseForm.isAnonymous()
                            ? "Response posted anonymously."
                            : "Response posted with your display name.",
                    "response", Map.of(
                            "id", response.getId(),
                            "author", response.isAnonymous() || response.getStudent().isDeleted()
                                    ? "Anonymous Helper #" + response.getId()
                                    : response.getStudent().getDisplayName(),
                            "body", response.getMessage(),
                            "createdAt", response.getCreatedAt().format(RESPONSE_TIME_FORMAT),
                            "helpfulCount", response.getHelpfulCount()
                    )
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "ok", false,
                    "message", ex.getMessage()
            ));
        }
    }

    @PostMapping("/requests/{id}/resolve")
    public String toggleResolved(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        helpRequestService.toggleResolved(id, currentUser(authentication));
        redirectAttributes.addFlashAttribute("success", "Request status updated.");
        return "redirect:/my-requests";
    }

    @PostMapping("/requests/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam RequestStatus status,
                               Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            helpRequestService.updateStatus(id, status, currentUser(authentication));
            redirectAttributes.addFlashAttribute("success", "Request workflow status updated.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("success", ex.getMessage());
        }
        return "redirect:/requests/" + id;
    }

    @PostMapping("/requests/{requestId}/responses/{responseId}/accept")
    public String acceptResponse(@PathVariable Long requestId, @PathVariable Long responseId,
                                 Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            helpRequestService.acceptResponse(requestId, responseId, currentUser(authentication));
            redirectAttributes.addFlashAttribute("success", "Accepted answer selected and request marked resolved.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("success", ex.getMessage());
        }
        return "redirect:/requests/" + requestId;
    }

    @PostMapping("/requests/{id}/save")
    public String saveRequest(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        boolean saved = helpRequestService.toggleSaved(id, currentUser(authentication));
        redirectAttributes.addFlashAttribute("success", saved ? "Request saved to your dashboard." : "Request removed from saved list.");
        return "redirect:/requests/" + id;
    }

    @PostMapping("/responses/{id}/helpful")
    public String helpfulResponse(@PathVariable Long id, @RequestParam Long requestId,
                                  Authentication authentication, RedirectAttributes redirectAttributes) {
        boolean added = responseService.markHelpful(id, currentUser(authentication));
        redirectAttributes.addFlashAttribute("success", added ? "Marked as helpful." : "You already marked this response helpful.");
        return "redirect:/requests/" + requestId;
    }

    @PostMapping("/requests/{id}/report")
    public String reportRequest(@PathVariable Long id, @Valid @ModelAttribute ReportForm reportForm,
                                BindingResult bindingResult, Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasErrors()) {
            reportService.report(ReportTargetType.REQUEST, id, reportForm, currentUser(authentication));
            redirectAttributes.addFlashAttribute("success", "Report submitted for moderator review.");
        }
        return "redirect:/requests/" + id;
    }

    @PostMapping("/responses/{id}/report")
    public String reportResponse(@PathVariable Long id, @RequestParam Long requestId,
                                 @Valid @ModelAttribute ReportForm reportForm,
                                 BindingResult bindingResult, Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasErrors()) {
            reportService.report(ReportTargetType.RESPONSE, id, reportForm, currentUser(authentication));
            redirectAttributes.addFlashAttribute("success", "Report submitted for moderator review.");
        }
        return "redirect:/requests/" + requestId;
    }

    private User currentUser(Authentication authentication) {
        return userService.requireByUsername(authentication.getName());
    }

    private void populateDetailsModel(Long id, User viewer, Model model) {
        HelpRequest request = helpRequestService.visibleRequest(id, viewer);
        model.addAttribute("request", request);
        model.addAttribute("viewer", viewer);
        model.addAttribute("saved", viewer != null && helpRequestService.isSaved(id, viewer));
        model.addAttribute("owner", viewer != null && request.getStudent().getId().equals(viewer.getId()));
        model.addAttribute("reportForm", new ReportForm());
        model.addAttribute("statuses", RequestStatus.values());
    }
}
