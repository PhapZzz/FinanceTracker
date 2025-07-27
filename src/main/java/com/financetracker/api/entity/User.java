package com.financetracker.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Data
@NoArgsConstructor // contrustor không tham số
@AllArgsConstructor // contrustor cótham số
@Builder
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class User {
    //note
    //orphanRemoval = true giúp xóa tự động các con khi xóa cha (ví dụ xóa category thì xóa hết budget liên quan).

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;


    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password; // bcrypt hash

    @Column(length = 512)
    private String avatar; // URL ảnh

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false, referencedColumnName = "role_id")
    private Role role;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // sử dụng Set để tránh trùng lặp. tránh việc user có 2 category_id giống nhau
    @OneToMany(mappedBy = "user")
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Transaction> transactions = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Budget> budgets = new HashSet<>();

    @OneToOne(mappedBy = "user")
    private NotificationSetting notificationSettings;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    //Trước khi insert (save entity mới)
    // được tạo khi insert
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


    @Column(name = "failed_attempts")
    private Integer failedAttempts = 0;

    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;


}
