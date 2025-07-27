package com.financetracker.api.service;

import com.financetracker.api.entity.Category;
import com.financetracker.api.entity.CategoryIcon;
import com.financetracker.api.entity.User;
import com.financetracker.api.enums.CategoryType;
import com.financetracker.api.exception.ResourceNotFoundException;
import com.financetracker.api.mapper.CategoryMapper;
import com.financetracker.api.repository.CategoryIconRepository;
import com.financetracker.api.repository.CategoryRepository;
import com.financetracker.api.repository.UserRepository;
import com.financetracker.api.request.CategoryRequest;
import com.financetracker.api.response.CategoryResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryIconRepository categoryIconRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;

    private CategoryIcon findCategoryIcon(CategoryRequest request) {
        if (request.getEmoji() != null && !request.getEmoji().isBlank()) {
            return categoryIconRepository
                    .findByEmoji(request.getEmoji())
                    .orElseThrow(() -> new ResourceNotFoundException("CategoryIcon not found for emoji"));
        }

        return categoryIconRepository
                .findByNameIgnoreCase(request.getName())
                .orElseGet(() -> categoryIconRepository
                        .findByEmoji("👤")
                        .orElseThrow(() -> new ResourceNotFoundException("Default icon 👤 not found")));
    }

    @Transactional
    public CategoryResponse addCategory(CategoryRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CategoryIcon icon = findCategoryIcon(request);

        boolean exists = categoryRepository.existsByCategoryIconNameIgnoreCaseAndUserId(
                request.getName().trim(), userId
        );

        if (exists) {
            throw new IllegalStateException("Category name already exists");
        }

        Category category = Category.builder()
                .user(user)
                .categoryIcon(icon)
                .type(request.getType()) //  type từ request lưu vào Category
                .build();

        categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    @Transactional
    public Map<String, List<CategoryResponse.Simple>> getCategories(CategoryType type, Long userId) {
        List<Category> categories;

        if (type != null) {
            categories = categoryRepository.findByUserIdAndType(userId, type);
            if (categories.isEmpty()) {
                throw new ResourceNotFoundException("No categories found for the given type");
            }

            // Trả về dạng Map cho đồng bộ format: { data: [...] }
            return Map.of("data", categories.stream()
                    .map(categoryMapper::toSimpleResponse)
                    .toList());
        }

        categories = categoryRepository.findByUserId(userId);

        return categories.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getType().name(), // "EXPENSE", "INCOME"
                        Collectors.mapping(categoryMapper::toSimpleResponse, Collectors.toList())
                ));
    }
}
