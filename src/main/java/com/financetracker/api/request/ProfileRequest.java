package com.financetracker.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;
    @Pattern(regexp = "^(https?|ftp)://[^\s]+$", message = "Invalid avatar URL")
    private String avatar;
    private String email;
}
