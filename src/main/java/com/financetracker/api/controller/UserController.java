package com.financetracker.api.controller;

import com.financetracker.api.request.ProfileRequest;
import com.financetracker.api.response.ApiResponse;
import com.financetracker.api.response.UserResponse;
import com.financetracker.api.security.CustomUserDetails;
import com.financetracker.api.security.Jwt.util.JwtTokenUtil;
import com.financetracker.api.service.serviceImpl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/user") // nên viết thường theo RESTful
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping("/token-expiration")
    public ResponseEntity<?> checkTokenExpiration(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("success", false, "message", "Missing token")
            );
        }

        String token = authHeader.substring(7);
        try {
            Instant expiration = jwtTokenUtil.getExpirationFromToken(token);
            boolean isExpired = expiration.isBefore(Instant.now());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "expired", isExpired,
                    "expirationTime", expiration.toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("success", false, "message", "Invalid token")
            );
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @Valid @RequestBody ProfileRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    )
    {
        Long userId = userDetails.getUser().getId();

        UserResponse updated = userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("User profile updated successfully", updated));
    }

}
