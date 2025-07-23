package com.financetracker.api.dto;

import com.financetracker.api.enums.CategoryType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {
    @NotBlank(message = "Category name is required")
    private String name;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Category type must be INCOME or EXPENSE")
    @Pattern(regexp = "INCOME|EXPENSE", message = "Category type must be INCOME or EXPENSE")
    private CategoryType type;
    private String iconUrl;
    private String emoji;
    private Long userId;
}
