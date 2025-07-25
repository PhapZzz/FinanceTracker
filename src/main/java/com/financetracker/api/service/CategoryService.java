package com.financetracker.api.service;

import com.financetracker.api.dto.CategoryDTO;
import com.financetracker.api.enums.CategoryType;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    CategoryDTO add(CategoryDTO categoryDTO);

    List<CategoryDTO> getCategoriesByType(CategoryType type);
    Map<CategoryType, List<CategoryDTO>> getAllCategoriesGroupedByType();
}