package com.financetracker.api.dto;

import com.financetracker.api.enums.CategoryType;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {
    private String name;
    private CategoryType type;
    private String emoji;
    private Long userId;
}
