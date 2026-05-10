package com.silenthelp.silenthelp.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "help_requests", indexes = {
        @Index(name = "idx_help_requests_created", columnList = "createdAt"),
        @Index(name = "idx_help_requests_title", columnList = "title")
})
public class HelpRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean anonymous = true;

    @Column(nullable = false, length = 20)
    private String urgency = "NORMAL";

    private LocalDate expectedBy;

    @Column(length = 120)
    private String preferredContact;

    @Column(length = 180)
    private String tags;

    @Column(length = 255)
    private String attachmentUrl;

    @Column(length = 120)
    private String campusGroup;

    @Column(nullable = false)
    private long viewCount = 0;

    @Column(nullable = false)
    private long responseCount = 0;

    @Column(nullable = false)
    private boolean resolved = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestStatus status = RequestStatus.OPEN;

    @Column(nullable = false)
    private boolean hidden = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accepted_response_id")
    private Response acceptedResponse;

    @OneToMany(mappedBy = "helpRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<Response> responses = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public LocalDate getExpectedBy() {
        return expectedBy;
    }

    public void setExpectedBy(LocalDate expectedBy) {
        this.expectedBy = expectedBy;
    }

    public String getPreferredContact() {
        return preferredContact;
    }

    public void setPreferredContact(String preferredContact) {
        this.preferredContact = preferredContact;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getCampusGroup() {
        return campusGroup;
    }

    public void setCampusGroup(String campusGroup) {
        this.campusGroup = campusGroup;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public long getResponseCount() {
        return responseCount;
    }

    public void incrementResponseCount() {
        this.responseCount++;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
        this.resolved = status == RequestStatus.RESOLVED;
    }

    public Response getAcceptedResponse() {
        return acceptedResponse;
    }

    public void setAcceptedResponse(Response acceptedResponse) {
        this.acceptedResponse = acceptedResponse;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Response> getResponses() {
        return responses;
    }
}
