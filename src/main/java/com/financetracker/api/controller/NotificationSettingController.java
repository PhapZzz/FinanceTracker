package com.financetracker.api.controller;

import com.financetracker.api.dto.request.NotificationSettingRequest;
import com.financetracker.api.dto.response.ApiResponse;
import com.financetracker.api.service.CustomUserDetails;
import com.financetracker.api.service.NotificationSettingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications/settings")
@RequiredArgsConstructor
public class NotificationSettingController {

    private final NotificationSettingService settingService;

    @GetMapping
    public ResponseEntity<?> getSettings(@AuthenticationPrincipal CustomUserDetails userDetails) {
        var response = settingService.getSettings(userDetails.getUser().getId());
        return ResponseEntity.ok(ApiResponse.success("Notification settings fetched successfully", response));
    }


    @PutMapping
    public ResponseEntity<?> updateSettings(@Valid @RequestBody NotificationSettingRequest request,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        var response = settingService.updateSettings(request, userDetails.getUser().getId());
        return ResponseEntity
                .ok(ApiResponse.success("Notification settings updated successfully", response));
    }
}
