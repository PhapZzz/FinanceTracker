package com.financetracker.api.security.Jwt.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtTokenProvider {
     @Value("${jwt.secret}")
    // Secret key để ký token, nên lưu trong file cấu hình
     private String secretKey;

    @Value("${jwt.expiration}")
    // Thời hạn token (ví dụ 1 giờ)
    private long expirationTime;

    // Tạo khóa ký từ SECRET_KEY
    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Hàm tạo token
    public String generateToken(String username, Map<String, Object> extraClaims) {
        return Jwts.builder()
                .setClaims(extraClaims) // các claims bổ sung như role, id...
                .setSubject(username)   // người dùng chính (thường là username hoặc userId)
                .setIssuedAt(new Date(System.currentTimeMillis())) // ngày tạo
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // hết hạn
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // ký với HMAC + SHA256
                .compact(); // tạo token
    }
}
