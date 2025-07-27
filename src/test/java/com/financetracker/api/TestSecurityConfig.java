package com.financetracker.api;

import com.financetracker.api.security.Jwt.filter.JwtAuthenticationFilter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestSecurityConfig {
    @Bean
    @Primary
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return mock(JwtAuthenticationFilter.class); // trả về filter mock để không chặn request
    }
}

