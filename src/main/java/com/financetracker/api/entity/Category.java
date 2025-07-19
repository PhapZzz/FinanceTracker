package com.financetracker.api.entity;

import com.financetracker.api.enums.CategoryType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private long id;

    @Column(name = "category_name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private CategoryType type;

    @Column (length = 10)
    private String emoji;

    @Column(name = "icon_url",length = 255)
    private String iconUrl;

    // 1 user có nhiều danh mục
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "user_id")
    private User user;


    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    @OneToMany(mappedBy = "category")
    private Set<Transaction> transactions = new HashSet<>();

    @OneToMany(mappedBy = "category")
    private Set<Budget> budgets = new HashSet<>();


    //Trước khi insert (save entity mới)
    // được tạo khi insert
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
