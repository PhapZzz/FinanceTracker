package com.financetracker.api.service;

import com.financetracker.api.request.LoginRequest;
import com.financetracker.api.request.ProfileRequest;
import com.financetracker.api.request.RegisterRequest;
import com.financetracker.api.response.LoginResponse;
import com.financetracker.api.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service

public interface UserService {
//    public void registerUser(RegisterRequest registerRequest) {
//
//        // Kiểm tra email đã tồn tại chưa
//        if (userRepository.existsByEmail(registerRequest.getEmail())) {
//            throw new AppException("Email đã tồn tại trong hệ thống.");
//        }
//
//        // Lấy role dựa theo ID được gửi từ client

    /// /        Role role = roleRepository.findById(registerRequest.getRoleId())
    /// /                .orElseThrow(() -> new AppException("Role không tồn tại."));
//
//        Role role = roleRepository.findByName(RoleName.USER)
//                .orElseThrow(() -> new AppException("ROLE_USER chưa được khởi tạo."));
//
//        // Map DTO → Entity và mã hóa mật khẩu
//        User user = userMapper.toEntity(registerRequest);
//        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
//        user.setRole(role); // ánh xạ vai trò
//
//        // Lưu vào CSDL
//        userRepository.save(user);
//    }
    public UserResponse registerUser(RegisterRequest registerRequest);



/*
    public LoginResponse loginUser(LoginRequest request) {
        // Tìm user theo email

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("Email or password is incorrect"));
//        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
//            throw new AppException("Email or password is incorrect");
//        }

        // So sánh mật khẩu
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException("Email or password is incorrect");
        }
        /*
        - Email chưa xác thực (nếu có 2FA/verify email) – 403 Forbidden
                {
                 "success": false,
                 "message": "Please verify your email before logging in"
                }*/

    /*
        if (!user.isVerified()) {
            throw new AppException("Please verify your email before logging in", HttpStatus.FORBIDDEN);
        }
        //phần này nhập pass sai quá 5 lần

        // Tạo JWT token
        String token = jwtTokenUtil.generateToken(user);
        Instant expireAt = jwtTokenUtil.getExpirationFromToken(token);

        // Trả đối tượng DTO chứa accessToken và thời hạn
        return LoginResponse.builder()
                .accessToken(token)
                .expiresToken(expireAt)
                .build();
    }*/

    public LoginResponse loginUser(LoginRequest request) ;

    UserResponse updateProfile(Long userId, @Valid ProfileRequest request);

}
