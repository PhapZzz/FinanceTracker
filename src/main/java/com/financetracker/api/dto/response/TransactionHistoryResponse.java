package com.financetracker.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionHistoryResponse {
    private Long id;
    private BigDecimal amount;
    private LocalDate date;
    private String note;
    private CategoryDTO category;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryDTO {
        private String name;
        private String emoji;
        private String iconUrl;
        private String type;
    }
}
