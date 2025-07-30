package com.financetracker.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class TransactionResponse {
    private Long id;
    private BigDecimal amount;
    private String note;
    private LocalDate date;
    private CategoryDTO category;

    @Getter
    @Setter
    @Builder
    public static class CategoryDTO {
        private Long id;
        private String name;
        private String type;
        private String emoji;
        private String iconUrl;
    }
}
