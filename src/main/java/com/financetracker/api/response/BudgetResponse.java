package com.financetracker.api.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Data
@Builder
public class BudgetResponse {

    private Long id;
    private BigDecimal amount;
    private int month;
    private int year;

    private CategoryDTO category;

    @Getter
    @Setter
    @Builder
    public static class CategoryDTO {
        private Long id;
        private String name;
        private String emoji;
        private String iconUrl;
        private String type; // INCOME/EXPENSE
    }
}
