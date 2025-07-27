package com.financetracker.api.security.Jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financetracker.api.security.Jwt.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService; //

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        // Bỏ qua xác thực token cho các endpoint công khai
        if (path.equals("/api/auth/register") || path.equals("/api/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            handleError(response, "Missing access token", HttpStatus.UNAUTHORIZED);
//            return;
//        }
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            String message = path.startsWith("/api/notifications/settings")
                    ? "Unauthorized – Please login to access notification settings"
                    : "Missing access token";

            handleError(response, message, HttpStatus.UNAUTHORIZED);
            return;
        }

        String token = authHeader.substring(7);
//        if (!jwtTokenUtil.validateTokenOrThrow(token)) {
//            handleError(response, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
//            return;
//        }
        if (!jwtTokenUtil.validateTokenOrThrow(token)) {
            String message = path.startsWith("/api/notifications/settings")
                    ? "Unauthorized – Invalid or expired token"
                    : "Invalid or expired token";

            handleError(response, message, HttpStatus.UNAUTHORIZED);
            return;
        }


        String email = jwtTokenUtil.getEmailFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    private void handleError(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(
                        Map.of("success", false, "message", message)
                )
        );
    }
}
