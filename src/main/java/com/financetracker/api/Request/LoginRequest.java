package com.financetracker.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "ko dc de trong")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Password must be contain both letters and numbers")
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự.")
    private String password;

}
