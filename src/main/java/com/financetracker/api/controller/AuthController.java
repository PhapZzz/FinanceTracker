package com.financetracker.api.controller;

import com.financetracker.api.dto.RegisterRequest;
import com.financetracker.api.response.SuccessResponse;
import com.financetracker.api.response.UserResponse;
import com.financetracker.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

//    @PostMapping("/register")
//    public ResponseEntity<SuccessResponse<String>> register(@RequestBody RegisterRequest request) {
//        userService.registerUser(request);
//        return ResponseEntity.ok(SuccessResponse.of(null, "Đăng ký tài khoản thành công."));
//    }
@PostMapping("/register")
public ResponseEntity<SuccessResponse<UserResponse>> register(@RequestBody RegisterRequest request) {
    UserResponse userResponse = userService.registerUser(request);

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(SuccessResponse.of(userResponse, "Registration successful"));
}
}
