package com.financetracker.api.controller;


import com.financetracker.api.request.CategoryRequest;
import com.financetracker.api.response.ApiResponse;
import com.financetracker.api.response.CategoryResponse;
import com.financetracker.api.security.CustomUserDetails;
import com.financetracker.api.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<?> addCategory(@Valid @RequestBody CategoryRequest request,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {

        CategoryResponse response = categoryService.addCategory(request, userDetails.getUser().getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", response));
    }
}
