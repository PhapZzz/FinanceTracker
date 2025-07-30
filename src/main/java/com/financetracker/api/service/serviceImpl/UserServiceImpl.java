package com.financetracker.api.service.serviceImpl;

import com.financetracker.api.entity.Role;
import com.financetracker.api.entity.User;
import com.financetracker.api.enums.RoleName;
import com.financetracker.api.exception.AppException;
import com.financetracker.api.exception.ResourceNotFoundException;
import com.financetracker.api.mapper.UserMapper;
import com.financetracker.api.repository.RoleRepository;
import com.financetracker.api.repository.UserRepository;
import com.financetracker.api.dto.request.LoginRequest;
import com.financetracker.api.dto.request.ProfileRequest;
import com.financetracker.api.dto.request.RegisterRequest;
import com.financetracker.api.dto.response.LoginResponse;
import com.financetracker.api.dto.response.UserResponse;
import com.financetracker.api.security.Jwt.util.JwtTokenUtil;
import com.financetracker.api.service.UserService;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Builder
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    //log in

    private final JwtTokenUtil jwtTokenUtil;


    private final PasswordEncoder passwordEncoder;


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
    public UserResponse registerUser(RegisterRequest registerRequest) {


        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new AppException("Email is already registered", HttpStatus.CONFLICT);
        }

        Role role = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new AppException("ROLE_USER chưa được khởi tạo."));

        User user = userMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(role);
        user.setVerified(true); // ❗️CHỈ DÙNG TRONG GIAI ĐOẠN TEST cho nó bằng true là hợp lệ

        User savedUser = userRepository.save(user);

        return userMapper.toUserResponse(savedUser);
    }
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

    public LoginResponse loginUser(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("Email or password is incorrect", HttpStatus.UNAUTHORIZED));

        //  Nếu tài khoản đang bị khóa tạm thời
        if (user.getAccountLockedUntil() != null &&
                user.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
            throw new AppException("Account is temporarily locked. Try again later.", HttpStatus.FORBIDDEN);
        }

        //  Nếu mật khẩu sai
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.setFailedAttempts(user.getFailedAttempts() + 1);


            if (user.getFailedAttempts() >= 2) {
                user.setAccountLockedUntil(LocalDateTime.now().plusSeconds(10));
            }

            userRepository.save(user);
            throw new AppException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        //  Nếu email chưa xác thực
        if (!user.isVerified()) {
            throw new AppException("Please verify your email before logging in", HttpStatus.FORBIDDEN);
        }

        // Đăng nhập đúng → reset failedAttempts
        user.setFailedAttempts(0);
        user.setAccountLockedUntil(null);
        userRepository.save(user);

        //  Tạo JWT
        String token = jwtTokenUtil.generateToken(user);
        Instant expiresToken = jwtTokenUtil.getExpirationFromToken(token);

        return LoginResponse.builder()
                .accessToken(token)
                .expire(expiresToken)
                .build();
    }

    @Transactional
    public UserResponse updateProfile(Long userId, ProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setFullName(request.getFullName());
        user.setAvatar(request.getAvatar());

        userRepository.save(user);

        return UserResponse
                .builder()

                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .avatar(user.getAvatar())

                .build();
    }

}
