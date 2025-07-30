package com.financetracker.api.service;

import com.financetracker.api.entity.NotificationSetting;
import com.financetracker.api.entity.User;
import com.financetracker.api.exception.ResourceNotFoundException;
import com.financetracker.api.repository.NotificationSettingRepository;
import com.financetracker.api.repository.UserRepository;
import com.financetracker.api.dto.request.NotificationSettingRequest;
import com.financetracker.api.dto.response.NotificationSettingResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationSettingService {

    private final NotificationSettingRepository notificationSettingRepository;
    private final UserRepository userRepository;

    public NotificationSettingResponse getSettings(Long userId) {
        NotificationSetting setting = notificationSettingRepository
                .findByUserId(userId)
                .orElseGet(() -> createDefaultSettings(userId));

        return toResponse(setting);
    }

    @Transactional
    public NotificationSettingResponse updateSettings(NotificationSettingRequest request, Long userId) {

        NotificationSetting setting = notificationSettingRepository
                .findByUserId(userId)
                .orElseGet(() -> createDefaultSettings(userId));

        setting.setDailyReminder(request.getDailyReminder());
        setting.setTipsEnabled(request.getTipsEnabled());
        setting.setBudgetAlert(request.getBudgetAlert());

        return toResponse(notificationSettingRepository.save(setting));
    }

    private NotificationSetting createDefaultSettings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        NotificationSetting setting = NotificationSetting.builder()
                .user(user)
                .dailyReminder(false)
                .tipsEnabled(false)
                .budgetAlert(false)
                .build();

        return notificationSettingRepository.save(setting);
    }

    private NotificationSettingResponse toResponse(NotificationSetting setting) {
        return NotificationSettingResponse.builder()
                .dailyReminder(setting.isDailyReminder())
                .tipsEnabled(setting.isTipsEnabled())
                .budgetAlert(setting.isBudgetAlert())
                .build();
    }
}

