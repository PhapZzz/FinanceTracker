package com.financetracker.api.service;

import com.financetracker.api.entity.NotificationSetting;
import com.financetracker.api.entity.User;
import com.financetracker.api.exception.ResourceNotFoundException;
import com.financetracker.api.repository.NotificationSettingRepository;
import com.financetracker.api.repository.UserRepository;
import com.financetracker.api.request.NotificationSettingRequest;
import com.financetracker.api.response.NotificationSettingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificationSettingServiceTest {

    @Mock
    private NotificationSettingRepository settingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationSettingService settingService;

    private User user;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).build();
    }

    @Test
    @DisplayName("Get settings - exists")
    void testGetSettingsExists() {
        NotificationSetting setting = NotificationSetting.builder()
                .user(user)
                .dailyReminder(true)
                .tipsEnabled(true)
                .budgetAlert(false)
                .build();

        when(settingRepository.findByUserId(1L)).thenReturn(Optional.of(setting));

        NotificationSettingResponse res = settingService.getSettings(1L);

        assertTrue(res.isDailyReminder());
        assertTrue(res.isTipsEnabled());
        assertFalse(res.isBudgetAlert());
    }

    @Test
    @DisplayName("Get settings - create default if not found")
    void testGetSettingsCreateDefault() {
        when(settingRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        NotificationSetting saved = NotificationSetting.builder()
                .user(user)
                .dailyReminder(false)
                .tipsEnabled(false)
                .budgetAlert(false)
                .build();

        when(settingRepository.save(any())).thenReturn(saved);

        NotificationSettingResponse res = settingService.getSettings(1L);

        assertFalse(res.isDailyReminder());
        assertFalse(res.isTipsEnabled());
        assertFalse(res.isBudgetAlert());
    }

    @Test
    @DisplayName("Update settings - create if not exists")
    void testUpdateSettingsCreateIfNotExist() {
        NotificationSettingRequest request = NotificationSettingRequest.builder()
                .dailyReminder(true)
                .tipsEnabled(false)
                .budgetAlert(true)
                .build();

        when(settingRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        NotificationSetting updated = NotificationSetting.builder()
                .user(user)
                .dailyReminder(true)
                .tipsEnabled(false)
                .budgetAlert(true)
                .build();

        when(settingRepository.save(any())).thenReturn(updated);

        NotificationSettingResponse res = settingService.updateSettings(request, 1L);

        assertTrue(res.isDailyReminder());
        assertFalse(res.isTipsEnabled());
        assertTrue(res.isBudgetAlert());
    }

    @Test
    @DisplayName("Update settings - user not found")
    void testUpdateSettingsUserNotFound() {
        NotificationSettingRequest request = NotificationSettingRequest.builder()
                .dailyReminder(true)
                .tipsEnabled(true)
                .budgetAlert(true)
                .build();

        when(settingRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> settingService.updateSettings(request, 1L));
    }
}
