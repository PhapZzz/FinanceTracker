package com.financetracker.api.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NotificationSettingResponse {
    private boolean dailyReminder;
    private boolean tipsEnabled;
    private boolean budgetAlert;
}
