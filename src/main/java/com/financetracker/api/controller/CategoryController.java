package com.financetracker.api.controller;

import com.financetracker.api.dto.CategoryDTO;

import com.financetracker.api.enums.CategoryType;
import com.financetracker.api.response.SuccessResponse;
import com.financetracker.api.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<?>> getCategories(
            @RequestParam(required = false) CategoryType type) {

        if (type != null) {
            List<CategoryDTO> categories = categoryService.getCategoriesByType(type);
            if (categories.isEmpty()) {
                return ResponseEntity.ok(SuccessResponse.of(
                        categories,
                        "No categories found for the given type"
                ));
            }
            return ResponseEntity.ok(SuccessResponse.of(
                    categories,
                    "Category list fetched successfully"
            ));
        } else {
            Map<CategoryType, List<CategoryDTO>> categories = categoryService.getAllCategoriesGroupedByType();
            return ResponseEntity.ok(SuccessResponse.of(
                    categories,
                    "Category list fetched successfully"
            ));
        }
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<CategoryDTO>> add(@Valid @RequestBody CategoryDTO categoryDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.of(categoryService.add(categoryDTO),"Category created successfully"));
    }


}