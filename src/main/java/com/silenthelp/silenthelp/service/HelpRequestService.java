package com.silenthelp.silenthelp.service;

import com.silenthelp.silenthelp.dto.HelpRequestForm;
import com.silenthelp.silenthelp.model.Category;
import com.silenthelp.silenthelp.model.HelpRequest;
import com.silenthelp.silenthelp.model.RequestStatus;
import com.silenthelp.silenthelp.model.RequestView;
import com.silenthelp.silenthelp.model.Response;
import com.silenthelp.silenthelp.model.SavedRequest;
import com.silenthelp.silenthelp.model.User;
import com.silenthelp.silenthelp.repository.CategoryRepository;
import com.silenthelp.silenthelp.repository.HelpRequestRepository;
import com.silenthelp.silenthelp.repository.RequestViewRepository;
import com.silenthelp.silenthelp.repository.ResponseRepository;
import com.silenthelp.silenthelp.repository.SavedRequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HelpRequestService {
    private final HelpRequestRepository helpRequestRepository;
    private final CategoryRepository categoryRepository;
    private final RequestViewRepository requestViewRepository;
    private final ResponseRepository responseRepository;
    private final SavedRequestRepository savedRequestRepository;

    public HelpRequestService(HelpRequestRepository helpRequestRepository, CategoryRepository categoryRepository,
                              RequestViewRepository requestViewRepository, ResponseRepository responseRepository,
                              SavedRequestRepository savedRequestRepository) {
        this.helpRequestRepository = helpRequestRepository;
        this.categoryRepository = categoryRepository;
        this.requestViewRepository = requestViewRepository;
        this.responseRepository = responseRepository;
        this.savedRequestRepository = savedRequestRepository;
    }

    @Transactional(readOnly = true)
    public Page<HelpRequest> browse(String category, String keyword, String urgency, String status, Pageable pageable) {
        String normalizedCategory = category == null || category.isBlank() ? null : category;
        String normalizedKeyword = keyword == null || keyword.isBlank() ? null : keyword.trim();
        String normalizedUrgency = urgency == null || urgency.isBlank() ? null : urgency;
        String normalizedStatus = status == null || status.isBlank() ? null : status;
        return helpRequestRepository.searchVisible(normalizedCategory, normalizedKeyword, normalizedUrgency, normalizedStatus, pageable);
    }

    @Transactional(readOnly = true)
    public Page<HelpRequest> browseForAdmin(String category, String keyword, Pageable pageable) {
        String normalizedCategory = category == null || category.isBlank() ? null : category;
        String normalizedKeyword = keyword == null || keyword.isBlank() ? null : keyword.trim();
        return helpRequestRepository.searchAllForAdmin(normalizedCategory, normalizedKeyword, pageable);
    }

    @Transactional
    public HelpRequest visibleRequest(Long id, User viewer) {
        HelpRequest request = helpRequestRepository.findWithCategoryByIdAndHiddenFalse(id).orElseThrow();
        if (viewer != null && !requestViewRepository.existsByHelpRequestAndViewer(request, viewer)) {
            RequestView view = new RequestView();
            view.setHelpRequest(request);
            view.setViewer(viewer);
            requestViewRepository.save(view);
            request.incrementViewCount();
        }
        return request;
    }

    @Transactional(readOnly = true)
    public List<HelpRequest> latest() {
        return helpRequestRepository.findTop6ByHiddenFalseAndResolvedFalseOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<HelpRequest> mine(User user) {
        return helpRequestRepository.findByStudentAndResolvedFalseOrderByCreatedAtDesc(user);
    }

    @Transactional
    public HelpRequest create(HelpRequestForm form, User student) {
        Category category = categoryRepository.findById(form.getCategoryId()).orElseThrow();
        HelpRequest request = new HelpRequest();
        request.setTitle(form.getTitle().trim());
        request.setDescription(form.getDescription().trim());
        request.setCategory(category);
        request.setStudent(student);
        request.setAnonymous(form.isAnonymous());
        request.setUrgency(normalizeUrgency(form.getUrgency()));
        request.setStatus(RequestStatus.OPEN);
        request.setExpectedBy(form.getExpectedBy());
        request.setPreferredContact(clean(form.getPreferredContact()));
        request.setTags(clean(form.getTags()));
        request.setAttachmentUrl(clean(form.getAttachmentUrl()));
        request.setCampusGroup(clean(form.getCampusGroup()));
        return helpRequestRepository.save(request);
    }

    private String normalizeUrgency(String urgency) {
        if (urgency == null || urgency.isBlank()) {
            return "NORMAL";
        }
        String value = urgency.trim().toUpperCase();
        return List.of("LOW", "NORMAL", "HIGH", "URGENT").contains(value) ? value : "NORMAL";
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    @Transactional
    public void toggleResolved(Long id, User user) {
        HelpRequest request = helpRequestRepository.findById(id).orElseThrow();
        if (!request.getStudent().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Only the request owner can update status.");
        }
        request.setStatus(request.isResolved() ? RequestStatus.OPEN : RequestStatus.RESOLVED);
        request.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public void updateStatus(Long id, RequestStatus status, User user) {
        HelpRequest request = helpRequestRepository.findById(id).orElseThrow();
        if (!request.getStudent().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Only the request owner can update status.");
        }
        request.setStatus(status);
        request.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public void acceptResponse(Long requestId, Long responseId, User user) {
        HelpRequest request = helpRequestRepository.findById(requestId).orElseThrow();
        if (!request.getStudent().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Only the request owner can accept an answer.");
        }
        Response response = responseRepository.findById(responseId).orElseThrow();
        if (!response.getHelpRequest().getId().equals(request.getId())) {
            throw new IllegalArgumentException("Response does not belong to this request.");
        }
        request.setAcceptedResponse(response);
        request.setStatus(RequestStatus.RESOLVED);
        request.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public boolean toggleSaved(Long requestId, User user) {
        HelpRequest request = helpRequestRepository.findById(requestId).orElseThrow();
        var existing = savedRequestRepository.findByHelpRequestAndUser(request, user);
        if (existing.isPresent()) {
            savedRequestRepository.delete(existing.get());
            return false;
        }
        SavedRequest savedRequest = new SavedRequest();
        savedRequest.setHelpRequest(request);
        savedRequest.setUser(user);
        savedRequestRepository.save(savedRequest);
        return true;
    }

    @Transactional(readOnly = true)
    public boolean isSaved(Long requestId, User user) {
        HelpRequest request = helpRequestRepository.findById(requestId).orElseThrow();
        return savedRequestRepository.existsByHelpRequestAndUser(request, user);
    }

    @Transactional(readOnly = true)
    public List<SavedRequest> savedBy(User user) {
        return savedRequestRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Transactional
    public void hide(Long id, boolean hidden) {
        HelpRequest request = helpRequestRepository.findById(id).orElseThrow();
        request.setHidden(hidden);
        request.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public long visibleCount() {
        return helpRequestRepository.countByHiddenFalse();
    }

    @Transactional(readOnly = true)
    public long resolvedCount() {
        return helpRequestRepository.countByResolvedTrue();
    }

    @Transactional(readOnly = true)
    public long statusCount(RequestStatus status) {
        return helpRequestRepository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public long acceptedAnswersBy(User user) {
        return helpRequestRepository.countByAcceptedResponseStudent(user);
    }
}
