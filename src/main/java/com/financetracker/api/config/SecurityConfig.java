package com.financetracker.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financetracker.api.repository.UserRepository;
import com.financetracker.api.security.Jwt.filter.JwtAuthenticationFilter;
import com.financetracker.api.security.Jwt.handler.JwtAccessDeniedHandler;
import com.financetracker.api.security.Jwt.handler.JwtAuthenticationEntryPoint;
import com.financetracker.api.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    //    private final JwtTokenUtil jwtTokenUtil; // THÊM DÒNG NÀY
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


//    @Bean
//    public UserDetailsService userDetailsService() {
//        return new CustomUserDetailsService(userRepository);
//    }

//    @Bean
//    public JwtAuthenticationFilter jwtAuthenticationFilter(CustomUserDetailsService customUserDetailsService) {
//        return new JwtAuthenticationFilter(jwtTokenUtil, customUserDetailsService);
//    }


    //rieng
    //  Custom handler trả về JSON khi bị cấm truy cập
//    @Bean
//    public AccessDeniedHandler customAccessDeniedHandler() {
//        return (req, res, ex) -> {
//            res.setStatus(HttpStatus.FORBIDDEN.value());
//            res.setContentType("application/json");
//            res.getWriter().write(new ObjectMapper().writeValueAsString(
//                    Map.of("success", false,
//                            "message", "Forbidden – You do not have permission to perform this action")
//            ));
//        };
//    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/user/token-expiration"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

//                .exceptionHandling(e -> e
//                        .authenticationEntryPoint((req, res, ex) -> {
//                            res.setStatus(HttpStatus.UNAUTHORIZED.value());
//                            res.setContentType("application/json");
//                            res.getWriter().write(new ObjectMapper().writeValueAsString(
//                                    Map.of("success", false,
//                                            "message", "Unauthorized – Please login to access this resource")
//                            ));
//                        }

//                        )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) // ✅ Dùng entry point riêng
                        .accessDeniedHandler(jwtAccessDeniedHandler)

                        .accessDeniedHandler((req, res, ex) -> {
                            res.setStatus(HttpStatus.FORBIDDEN.value());
                            res.setContentType("application/json");
                            res.getWriter().write(new ObjectMapper().writeValueAsString(
                                    Map.of("success", false,
                                            "message", "Forbidden – You do not have permission to perform this action")
                            ));
                        })

                )


                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

}