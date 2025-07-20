package com.financetracker.api.dto;

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

    @NotBlank(message = "Họ tên không được để trống.")
    private String fullName;

    @NotBlank(message = "Email không được để trống.")
    @Email(message = "Email không đúng định dạng.")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Password must be contain both letters and numbers")
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự.")
    private String password;


    private Long roleId; // Optional nếu mặc định USER

}
