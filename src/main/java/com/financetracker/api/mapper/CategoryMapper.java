package com.financetracker.api.mapper;

import com.financetracker.api.dto.CategoryDTO;
import com.financetracker.api.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "name", source = "categoryIcon.name")
    @Mapping(target = "emoji", source = "categoryIcon.emoji")
    @Mapping(target = "iconUrl", source = "categoryIcon.iconUrl")
    public CategoryDTO toDTO(Category category);

    @Mapping(target = "user.id", source = "userId")
    public Category toEntity(CategoryDTO categoryDTO);


}