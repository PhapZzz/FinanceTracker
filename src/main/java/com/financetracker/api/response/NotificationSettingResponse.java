package com.financetracker.api.response;

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
