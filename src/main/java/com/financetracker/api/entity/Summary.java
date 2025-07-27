package com.financetracker.api.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Immutable // optional chỉ đọc view
@Table(name = "summary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Subselect("SELECT * FROM your_view_name")
public class Summary {

    @Id
    @Column(name = "trasaction_id")
    private Long transactionId;

    private Integer year;
    private Integer month;
    private Integer day;

    private BigDecimal amount;

    private String note;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "user_id")
    private Long userId;

    private String emoji;

    @Column(name = "icon_url")
    private String iconUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private CascadeType type;
}

