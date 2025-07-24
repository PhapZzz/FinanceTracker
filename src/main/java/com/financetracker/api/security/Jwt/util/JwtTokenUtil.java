package com.financetracker.api.security.Jwt.util;

import com.financetracker.api.entity.User;
import com.financetracker.api.exception.JwtAuthenticationException;
import com.financetracker.api.security.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTimeInMs;

    // tạo token
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTimeInMs);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().getName().name())
                .claim("userId", user.getId())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ký key bí mật
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // ký xong sẽ set và lấy nó
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // set ký và gán ký key bí mật ở trên
                .build()
                .parseClaimsJws(token) // lấy token
                .getBody();
    }

    // kiểm tra hợp lệ token (ko quá hạn , ko đóng format, bị làm giả, token null)
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            System.out.println("JWT validation failed: " + e.getMessage()); // ➕ log lỗi ra console
            return false;
        }
    }


    // Hoặc ném lỗi chi tiết
    public boolean validateTokenOrThrow(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException("Token has expired", HttpStatus.UNAUTHORIZED);
        } catch (MalformedJwtException e) {
            throw new JwtAuthenticationException("Invalid token format", HttpStatus.UNAUTHORIZED);
        } catch (SignatureException e) {
            throw new JwtAuthenticationException("Token signature is invalid", HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            throw new JwtAuthenticationException("Missing access token", HttpStatus.UNAUTHORIZED);
        }

    }

    // giải mã token để lấy thông tin user
    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    // lấy time hết hạn của token
    public Instant getExpirationFromToken(String token) {
        return parseClaims(token).getExpiration().toInstant();
    }


    // lấy userID
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUser().getId();
        }
        return null;
    }
}
