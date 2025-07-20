package com.financetracker.api.service;

import com.financetracker.api.dto.RegisterRequest;
import com.financetracker.api.entity.Role;
import com.financetracker.api.entity.User;
import com.financetracker.api.enums.RoleName;
import com.financetracker.api.exception.AppException;
import com.financetracker.api.mapper.UserMapper;
import com.financetracker.api.repository.RoleRepository;
import com.financetracker.api.repository.UserRepository;
import com.financetracker.api.response.UserResponse;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    private final PasswordEncoder passwordEncoder;


//    public void registerUser(RegisterRequest registerRequest) {
//
//        // Kiểm tra email đã tồn tại chưa
//        if (userRepository.existsByEmail(registerRequest.getEmail())) {
//            throw new AppException("Email đã tồn tại trong hệ thống.");
//        }
//
//        // Lấy role dựa theo ID được gửi từ client
////        Role role = roleRepository.findById(registerRequest.getRoleId())
////                .orElseThrow(() -> new AppException("Role không tồn tại."));
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
            throw new AppException("Email đã tồn tại trong hệ thống.");
        }

        Role role = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new AppException("ROLE_USER chưa được khởi tạo."));

        User user = userMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(role);

        User savedUser = userRepository.save(user);

        return userMapper.toUserResponse(savedUser);
    }

}
