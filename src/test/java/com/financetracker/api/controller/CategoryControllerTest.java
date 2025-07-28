package com.financetracker.api.controller;

import com.financetracker.api.entity.User;
import com.financetracker.api.enums.CategoryType;
import com.financetracker.api.exception.ResourceNotFoundException;
import com.financetracker.api.request.CategoryRequest;
import com.financetracker.api.response.ApiResponse;
import com.financetracker.api.response.CategoryResponse;
import com.financetracker.api.security.CustomUserDetails;
import com.financetracker.api.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCategoriesWithoutType_ShouldReturnGroupedCategories() {
        // Arrange
        Map<String, List<CategoryResponse.Simple>> grouped = Map.of(
                "EXPENSE", List.of(CategoryResponse.Simple.builder()
                        .id(1L).name("Food").icon("🍔").iconUrl("url1").build()),
                "INCOME", List.of(CategoryResponse.Simple.builder()
                        .id(2L).name("Salary").icon("💰").iconUrl("url2").build())
        );

        CustomUserDetails userDetails = new CustomUserDetails(
                com.financetracker.api.entity.User.builder().id(1L).build());

        when(categoryService.getCategories(null, 1L)).thenReturn(grouped);

        // Act
        ResponseEntity<?> response = categoryController.getCategories(null, userDetails);

        // Assert
        assertEquals(200, response.getStatusCodeValue());

        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertTrue(body.isSuccess());
        assertEquals("Category list fetched successfully", body.getMessage());
        assertEquals(grouped, body.getData());

        verify(categoryService).getCategories(null, 1L);
    }
    @Test
    void testGetCategoriesWithType_ShouldReturnList() {
        List<CategoryResponse.Simple> expectedList = List.of(
                CategoryResponse.Simple.builder()
                        .id(1L).name("Food").icon("🍔").iconUrl("url1").build()
        );

        CustomUserDetails userDetails = new CustomUserDetails(
                User.builder().id(1L).build());

        when(categoryService.getCategories(CategoryType.EXPENSE, 1L))
                .thenReturn(Map.of("data", expectedList));

        // Act
        ResponseEntity<?> response = categoryController.getCategories(CategoryType.EXPENSE, userDetails);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertTrue(body.isSuccess());
        assertEquals("Category list fetched successfully", body.getMessage());
        assertEquals(expectedList, body.getData());
    }
    @Test
    void testGetCategoriesWithType_ShouldReturnEmptyList() {
        CustomUserDetails userDetails = new CustomUserDetails(User.builder().id(1L).build());

        when(categoryService.getCategories(CategoryType.INCOME, 1L))
                .thenThrow(new ResourceNotFoundException("No data"));

        // Act
        ResponseEntity<?> response = categoryController.getCategories(CategoryType.INCOME, userDetails);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertTrue(body.isSuccess());
        assertEquals("No categories found for the given type", body.getMessage());
        assertEquals(List.of(), body.getData());
    }
    @Test
    void testAddCategory_Success() {
        CustomUserDetails userDetails = new CustomUserDetails(User.builder().id(1L).build());

        CategoryRequest request = new CategoryRequest();
        request.setName("Food");
        request.setType(CategoryType.EXPENSE);
        request.setEmoji("🍔");

        CategoryResponse responseDto = CategoryResponse.builder()
                .id(10L).name("Food").type(CategoryType.EXPENSE).icon("🍔").iconUrl("url.png")
                .build();

        when(categoryService.addCategory(request, 1L)).thenReturn(responseDto);

        // Act
        ResponseEntity<?> response = categoryController.addCategory(request, userDetails);

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertTrue(body.isSuccess());
        assertEquals("Category created successfully", body.getMessage());
        assertEquals(responseDto, body.getData());
    }

}
