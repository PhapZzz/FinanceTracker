package com.financetracker.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Invalid email format")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password must be at least 8 characters and contain both letters and numbers")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Password must be at least 8 characters and contain both letters and numbers")

    @Size(min = 8, message ="Password must be at least 8 characters and contain both letters and numbers")
    private String password;



    private Long roleId; // Optional nếu mặc định USER

}
