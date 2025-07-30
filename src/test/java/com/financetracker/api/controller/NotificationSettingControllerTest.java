package com.financetracker.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financetracker.api.enums.RoleName;
import com.financetracker.api.dto.request.NotificationSettingRequest;
import com.financetracker.api.dto.response.NotificationSettingResponse;
import com.financetracker.api.service.CustomUserDetails;
import com.financetracker.api.service.NotificationSettingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NotificationSettingController.class)
public class NotificationSettingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationSettingService settingService;

    private CustomUserDetails userDetails;

    @BeforeEach
    void setup() {
        userDetails = new CustomUserDetails(
                com.financetracker.api.entity.User.builder()
                        .id(1L)
                        .email("user@example.com")
                        .password("pass")
                        .role(com.financetracker.api.entity.Role.builder().name(RoleName.USER).build())
                        .build()
        );
    }

    @Test
    @DisplayName("GET /api/notifications/settings - success")
    void testGetSettingsSuccess() throws Exception {
        NotificationSettingResponse response = NotificationSettingResponse.builder()
                .dailyReminder(true)
                .tipsEnabled(false)
                .budgetAlert(true)
                .build();

        when(settingService.getSettings(1L)).thenReturn(response);

        mockMvc.perform(get("/api/notifications/settings")
                        .with(authentication(new TestingAuthenticationToken(userDetails, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dailyReminder").value(true))
                .andExpect(jsonPath("$.data.tipsEnabled").value(false))
                .andExpect(jsonPath("$.data.budgetAlert").value(true));
    }

    @Test
    @DisplayName("PUT /api/notifications/settings - success")
    void testUpdateSettingsSuccess() throws Exception {
        NotificationSettingRequest request = NotificationSettingRequest.builder()
                .dailyReminder(false)
                .tipsEnabled(true)
                .budgetAlert(false)
                .build();

        NotificationSettingResponse response = NotificationSettingResponse.builder()
                .dailyReminder(false)
                .tipsEnabled(true)
                .budgetAlert(false)
                .build();

        when(settingService.updateSettings(request, 1L)).thenReturn(response);

        mockMvc.perform(put("/api/notifications/settings")
                        .with(authentication(new TestingAuthenticationToken(userDetails, null)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dailyReminder").value(false))
                .andExpect(jsonPath("$.data.tipsEnabled").value(true))
                .andExpect(jsonPath("$.data.budgetAlert").value(false));
    }
}
