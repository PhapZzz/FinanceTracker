package com.financetracker.api.controller;


import com.financetracker.api.enums.CategoryType;
import com.financetracker.api.exception.ResourceNotFoundException;
import com.financetracker.api.mapper.CategoryMapper;
import com.financetracker.api.dto.request.CategoryRequest;
import com.financetracker.api.dto.response.ApiResponse;
import com.financetracker.api.dto.response.CategoryResponse;
import com.financetracker.api.service.CustomUserDetails;
import com.financetracker.api.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @PostMapping
    public ResponseEntity<?> addCategory(@Valid @RequestBody CategoryRequest request,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {

        CategoryResponse response = categoryService.addCategory(request, userDetails.getUser().getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", response));
    }

    //    @GetMapping
//    public ResponseEntity<SuccessResponse<?>> getCategories(
//            @RequestParam(required = false) String type) {
//
//
//        if (type != null && !type.isEmpty()) {
//            CategoryType categoryType = CategoryType.valueOf(type.toUpperCase());
//            List<CategoryResponse> categories = categoryService.getCategoriesByType(categoryType);
//
//            if (categories.isEmpty()) {
//                return ResponseEntity.ok(SuccessResponse.of(
//                        categories,
//                        "No categories found for the given type"
//                ));
//            }
//            return ResponseEntity.ok(SuccessResponse.of(
//                    categories,
//                    "Category list fetched successfully"
//            ));
//        } else {
//            Map<CategoryType, List<CategoryResponse>> categories =
//                    categoryService.getAllCategoriesGroupedByType();
//            return ResponseEntity.ok(SuccessResponse.of(
//                    categories,
//                    "Category list fetched successfully"
//            ));
//        }
    @GetMapping
    public ResponseEntity<?> getCategories(
            @RequestParam(name = "type", required = false) CategoryType type,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();

        try {
            Map<String, List<CategoryResponse.Simple>> result = categoryService.getCategories(type, userId);

            if (type != null) {
                List<CategoryResponse.Simple> data = result.get("data");
                String message = data.isEmpty()
                        ? "No categories found for the given type"
                        : "Category list fetched successfully";
                return ResponseEntity.ok(ApiResponse.success(message, data));
            }

            return ResponseEntity.ok(ApiResponse.success("Category list fetched successfully", result));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.ok(ApiResponse.success("No categories found for the given type", List.of()));
        }
    }
}
