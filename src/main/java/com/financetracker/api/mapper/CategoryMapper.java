package com.financetracker.api.mapper;

import com.financetracker.api.entity.Category;
import com.financetracker.api.entity.CategoryIcon;
import com.financetracker.api.dto.response.CategoryResponse;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        CategoryIcon icon = category.getCategoryIcon();

        return CategoryResponse.builder()
                .id(category.getId())
                .name(icon.getName())              // name vẫn lấy từ icon
                .type(category.getType())          // type lấy từ Category
                .icon(icon.getEmoji())
                .iconUrl(icon.getIconUrl())
                .build();
    }

    // ch đơn giản
    public CategoryResponse.Simple toSimpleResponse(Category category) {
        CategoryIcon icon = category.getCategoryIcon();

        return CategoryResponse.Simple.builder()
                .id(category.getId())
                .name(icon.getName())
                .icon(icon.getEmoji())
                .iconUrl(icon.getIconUrl())
                .build();
    }
}