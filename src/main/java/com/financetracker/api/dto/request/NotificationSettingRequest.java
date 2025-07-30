package com.financetracker.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationSettingRequest {
    @NotNull(message = "Must be true or false")
    private Boolean dailyReminder;

    @NotNull(message = "Must be true or false")
    private Boolean tipsEnabled;

    @NotNull(message = "Must be true or false")
    private Boolean budgetAlert;
}
