package com.financetracker.api;

import com.financetracker.api.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {
    public static Long getCurrentUserId() {
        CustomUserDetails userDetails = (CustomUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUser().getId();
    }
}