package com.financetracker.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.financetracker.api.enums.CategoryType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentTransactionsDTO {
    private Long id;
    private String category;
    private String icon; // hoặc emoji / iconUrl tuỳ nhu cầu
    private BigDecimal amount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private CategoryType type;
}
