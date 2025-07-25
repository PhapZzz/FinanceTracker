package com.financetracker.api.request;

import com.financetracker.api.enums.CategoryType;
import com.financetracker.api.response.ValidCategoryName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {
    @ValidCategoryName
    @NotBlank(message = "Category name is required")
    private String name;

    @NotNull(message = "Category type must be INCOME or EXPENSE")
    private CategoryType type;


    private String emoji;

}
