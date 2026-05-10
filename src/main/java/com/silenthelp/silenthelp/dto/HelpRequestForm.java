package com.silenthelp.silenthelp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class HelpRequestForm {
    @NotBlank
    @Size(min = 8, max = 150)
    private String title;

    @NotBlank
    @Size(min = 20, max = 5000)
    private String description;

    @NotNull
    private Long categoryId;

    @Size(max = 20)
    private String urgency = "NORMAL";

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate expectedBy;

    @Size(max = 120)
    private String preferredContact;

    @Size(max = 180)
    private String tags;

    @Size(max = 255)
    private String attachmentUrl;

    @Size(max = 120)
    private String campusGroup;

    private boolean anonymous = false;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
    public LocalDate getExpectedBy() { return expectedBy; }
    public void setExpectedBy(LocalDate expectedBy) { this.expectedBy = expectedBy; }
    public String getPreferredContact() { return preferredContact; }
    public void setPreferredContact(String preferredContact) { this.preferredContact = preferredContact; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
    public String getCampusGroup() { return campusGroup; }
    public void setCampusGroup(String campusGroup) { this.campusGroup = campusGroup; }
    public boolean isAnonymous() { return anonymous; }
    public void setAnonymous(boolean anonymous) { this.anonymous = anonymous; }
}
