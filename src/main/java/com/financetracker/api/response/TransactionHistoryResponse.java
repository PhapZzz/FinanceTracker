package com.financetracker.api.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TransactionHistoryResponse {
    private Long id;
    private BigDecimal amount;
    private LocalDate date;
    private CategoryDTO category;

    @Data
    @Builder
    public static class CategoryDTO {
        private String name;
        private String emoji;
        private String iconUrl;
        private String type;
    }
}
