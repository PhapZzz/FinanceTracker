package com.financetracker.api.request;

import jakarta.validation.constraints.NotBlank;

public class ProfileRequest {
    @NotBlank
    private String fullName;
    private String avatar;
    private String email;
}
