package com.financetracker.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    // Optional nhưng nếu có thì phải đúng định dạng URL
    @Pattern(regexp = "^(https?|ftp)://[^\\s]+$", message = "Invalid avatar URL")
    private String avatar;
}
