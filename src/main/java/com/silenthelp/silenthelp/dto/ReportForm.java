package com.silenthelp.silenthelp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ReportForm {
    @NotBlank
    @Size(max = 180)
    private String reason;

    @Size(max = 2000)
    private String details;

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
