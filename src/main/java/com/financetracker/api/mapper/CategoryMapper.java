package com.financetracker.api.mapper;

import com.financetracker.api.entity.Category;
import com.financetracker.api.response.CategoryResponse;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {

        return CategoryResponse
                .builder()

                .id(category.getId())
                .name(category.getCategoryIcon().getName())
                .type(category.getType().name())
                .icon(category.getCategoryIcon().getEmoji())
                .iconUrl(category.getCategoryIcon().getIconUrl())

                .build();
    }
}