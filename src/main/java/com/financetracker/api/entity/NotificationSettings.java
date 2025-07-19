package com.financetracker.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// cài đặt thông báo
public class NotificationSettings {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    Long id;

    //1 user cho 1 setting
    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "user_id",nullable = false)
    private User user;

    @Column(name = "daily_reminder", nullable = false)
    private boolean dailyReminder = false;

    @Column(name = "tips_enabled", nullable = false)
    private boolean tipsEnabled = false;

    @Column(name = "budget_alert", nullable = false)
    private boolean budgetAlert = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}


