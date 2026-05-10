package com.silenthelp.silenthelp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResponseForm {
    @NotBlank
    @Size(min = 5, max = 3000)
    private String message;

    private boolean anonymous = false;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isAnonymous() { return anonymous; }
    public void setAnonymous(boolean anonymous) { this.anonymous = anonymous; }
}
