package com.financetracker.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    private String fullName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    private String avatar;
    // URL ảnh
    private Long roleId;

}
