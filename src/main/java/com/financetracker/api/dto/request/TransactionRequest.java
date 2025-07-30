package com.financetracker.api.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Size(max = 255, message = "Note must not exceed 255 characters")
    private String note;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Date is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in yyyy-MM-dd format")
    private String date;
}

