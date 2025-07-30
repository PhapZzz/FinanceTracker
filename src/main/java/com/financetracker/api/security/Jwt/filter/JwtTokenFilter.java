package com.financetracker.api.security.Jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financetracker.api.service.CustomUserDetailsService;
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
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        if (token != null) {
            try {
                jwtTokenUtil.validateTokenOrThrow(token); // Ném lỗi chi tiết nếu sai

                String email = jwtTokenUtil.getEmailFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            } catch (Exception ex) {
                // Token không hợp lệ → trả về lỗi chi tiết, KHÔNG gọi filter tiếp
                handleError(response, ex.getMessage(), HttpStatus.UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer")) {
            return bearer.substring(7);
        }
        return null;
    }

    private void handleError(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        // Sử dụng LinkedHashMap để giữ thứ tự key
        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("success", false);
        errorBody.put("message", message);

        response.getWriter().write(new ObjectMapper().writeValueAsString(errorBody));
    }
}
