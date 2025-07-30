package com.financetracker.api.service;

import com.financetracker.api.entity.Role;
import com.financetracker.api.entity.User;
import com.financetracker.api.enums.RoleName;
import com.financetracker.api.exception.AppException;
import com.financetracker.api.exception.ResourceNotFoundException;
import com.financetracker.api.mapper.UserMapper;
import com.financetracker.api.repository.RoleRepository;
import com.financetracker.api.repository.UserRepository;
import com.financetracker.api.request.LoginRequest;
import com.financetracker.api.request.ProfileRequest;
import com.financetracker.api.request.RegisterRequest;
import com.financetracker.api.response.LoginResponse;
import com.financetracker.api.response.UserResponse;
import com.financetracker.api.security.Jwt.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequest request;
    private User user;
    private Role role;
    private UserResponse response;

    @BeforeEach
    void setUp() {
        request = new RegisterRequest("Test User", "test@example.com", "Password123", null);
        role = new Role();
        role.setId(1L);
        role.setName(RoleName.USER);

        user = new User();
        user.setId(1L);
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword("encodedPass");
        user.setRole(role);

        response = UserResponse.builder()
                .userId(1L)
                .email("test@example.com")
                .fullName("Test User")
                .avatar(null)
                .build();
    }

    //register
    @Test
    void registerUser_shouldSucceed_whenEmailIsNew() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.of(role));
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(response);

        UserResponse result = userService.registerUser(request);

        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_shouldThrow_whenEmailAlreadyExists() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        AppException ex = assertThrows(AppException.class, () -> userService.registerUser(request));
        assertThat(ex.getMessage()).isEqualTo("Email is already registered");
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void registerUser_shouldThrow_whenRoleNotFound() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> userService.registerUser(request));
        assertThat(ex.getMessage()).isEqualTo("ROLE_USER chưa được khởi tạo.");
    }

    //login
    @Test
    void loginUser_shouldLoginSuccessfully() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "Password123");
        user.setVerified(true);
        user.setFailedAttempts(1);
        user.setAccountLockedUntil(null);

        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("Password123", user.getPassword())).thenReturn(true);
        when(jwtTokenUtil.generateToken(user)).thenReturn("fake-jwt-token");
        when(jwtTokenUtil.getExpirationFromToken("fake-jwt-token")).thenReturn(Instant.now().plusSeconds(3600));

        LoginResponse response = userService.loginUser(loginRequest);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getAccessToken());
        verify(userRepository).save(any(User.class)); // reset failedAttempts
    }

    @Test
    void loginUser_shouldThrow_whenEmailNotFound() {
        LoginRequest loginRequest = new LoginRequest("wrong@example.com", "Password123");
        when(userRepository.findByEmail("wrong@example.com")).thenReturn(java.util.Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            userService.loginUser(loginRequest);
        });

        assertEquals("Email or password is incorrect", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    //
    @Test
    void loginUser_shouldThrow_whenPasswordIncorrect() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "WrongPassword");

        user.setFailedAttempts(1);
        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("WrongPassword", user.getPassword())).thenReturn(false);

        AppException exception = assertThrows(AppException.class, () -> {
            userService.loginUser(loginRequest);
        });

        assertEquals("Invalid email or password", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        verify(userRepository).save(any(User.class)); // cập nhật failedAttempts
    }

    @Test
    void loginUser_shouldThrow_whenAccountLocked() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "Password123");

        user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(5)); // vẫn còn khóa
        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(user));

        AppException exception = assertThrows(AppException.class, () -> {
            userService.loginUser(loginRequest);
        });

        assertEquals("Account is temporarily locked. Try again later.", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void loginUser_shouldThrow_whenEmailNotVerified() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "Password123");

        user.setVerified(false);
        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("Password123", user.getPassword())).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> {
            userService.loginUser(loginRequest);
        });

        assertEquals("Please verify your email before logging in", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    //update profile
    @Test
    void updateProfile_shouldUpdateSuccessfully() {
        Long userId = 1L;
        ProfileRequest profileRequest = new ProfileRequest("New Name", "https://cdn.example.com/avatar.png");

        user.setId(userId);
        user.setEmail("test@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponse result = userService.updateProfile(userId, profileRequest);

        assertNotNull(result);
        assertEquals("New Name", result.getFullName());
        assertEquals("https://cdn.example.com/avatar.png", result.getAvatar());
        assertEquals("test@example.com", result.getEmail());

        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_shouldThrow_whenUserNotFound() {
        Long userId = 99L;
        ProfileRequest profileRequest = new ProfileRequest("Name", null);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateProfile(userId, profileRequest);
        });

        verify(userRepository, never()).save(any());
    }


}
