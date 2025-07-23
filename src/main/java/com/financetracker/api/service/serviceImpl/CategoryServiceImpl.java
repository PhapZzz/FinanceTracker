package com.financetracker.api.service.serviceImpl;

import com.financetracker.api.dto.CategoryDTO;
import com.financetracker.api.entity.Category;
import com.financetracker.api.enums.CategoryType;
import com.financetracker.api.mapper.CategoryMapper;
import com.financetracker.api.repository.CategoryRepository;
import com.financetracker.api.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository,CategoryMapper categoryMapper){
        this.categoryRepository = categoryRepository;
        this.categoryMapper =categoryMapper;
    }
    public static long getUserID(){


        return 1;
    }

    @Override
    public CategoryDTO add(CategoryDTO categoryDTO){
        Long userid = getUserID();
        categoryDTO.setUserId(userid);
        Category category = categoryRepository.save(categoryMapper.toEntity(categoryDTO));
        return categoryMapper.toDTO(category);

    }

    @Override
    public List<CategoryDTO> getCategoriesByType(CategoryType type) {
        Long userId = getUserID();
        return categoryRepository.findByUserIdAndType(userId, type)
                .stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<CategoryType, List<CategoryDTO>> getAllCategoriesGroupedByType() {
        Long userId = getUserID();
        List<CategoryDTO> categories = categoryRepository.findByUserId(userId)
                .stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());

        return categories.stream()
                .collect(Collectors.groupingBy(CategoryDTO::getType));
    }
}