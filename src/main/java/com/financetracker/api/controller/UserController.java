package com.financetracker.api.controller;

import com.financetracker.api.security.Jwt.util.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/user") // nên viết thường theo RESTful
@RequiredArgsConstructor
public class UserController {

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
}
