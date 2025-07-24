package com.financetracker.api.security.Jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {


    private static final long EXPIRE_DURATION = 900000; // 86.400s = 1 ngày ,1 phut = 60 giay = 60000mili

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)// ai là người dùng
                .setIssuer("finance-tracker")// ai tạo token
                .setIssuedAt(new Date())// ai tạo token
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))// thời gian hết hạn
                .signWith(key) //ký với key bí mật
                .compact();
    }
}
