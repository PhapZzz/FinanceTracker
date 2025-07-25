package com.financetracker.api.service;

import com.financetracker.api.entity.Category;
import com.financetracker.api.entity.CategoryIcon;
import com.financetracker.api.entity.User;
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

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryIconRepository categoryIconRepository;
    private final UserRepository userRepository;

    private final CategoryMapper categoryMapper;



    private CategoryIcon findCategoryIcon(CategoryRequest request) {
        if (request.getEmoji() != null && !request.getEmoji().isBlank()) {
            // ✅ Sửa: dùng đúng method hợp lệ
            return categoryIconRepository
                    .findByEmoji(request.getEmoji())
                    .orElseThrow(() -> new ResourceNotFoundException("CategoryIcon not found for emoji"));
        }

        // Nếu không có emoji → tra theo name
        return categoryIconRepository
                .findByNameIgnoreCase(request.getName())
                .orElseGet(() -> categoryIconRepository
                        .findByEmoji("👤")
                        .orElseThrow(() -> new ResourceNotFoundException("Default icon 👤 not found")));
    }

    @Transactional
    public CategoryResponse addCategory(CategoryRequest request, Long userId) {
        // 1. Kiểm tra user tồn tại
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2. Tìm Categoryicon phù hợp trước
        CategoryIcon icon = findCategoryIcon(request);

        // 3. Kiểm tra tên danh mục có bị trùng trong user (dựa trên icon.name)
        boolean exists = categoryRepository.existsByCategoryIconNameIgnoreCaseAndUserId(
                request.getName().trim(), userId
        );

        if (exists) {
            throw new IllegalStateException("Category name already exists");
        }
        // 4. Tạo entity
        Category category = Category
                .builder()

                .user(user)
                .categoryIcon(icon)
                .type(request.getType())
                .build();

        categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }


}
