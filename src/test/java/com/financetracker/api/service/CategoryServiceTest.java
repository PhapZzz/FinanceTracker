package com.financetracker.api.service;

import com.financetracker.api.controller.CategoryController;
import com.financetracker.api.entity.Category;
import com.financetracker.api.entity.CategoryIcon;
import com.financetracker.api.entity.User;
import com.financetracker.api.enums.CategoryType;
import com.financetracker.api.exception.ResourceNotFoundException;
import com.financetracker.api.mapper.CategoryMapper;
import com.financetracker.api.repository.CategoryIconRepository;
import com.financetracker.api.repository.CategoryRepository;
import com.financetracker.api.repository.UserRepository;
import com.financetracker.api.dto.request.CategoryRequest;
import com.financetracker.api.dto.response.CategoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {
@Mock
private CustomUserDetailsService customUserDetailsService;
@Mock
private CustomUserDetails customUserDetails;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryIconRepository categoryIconRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryController categoryController;

    @InjectMocks
    private CategoryService categoryService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddCategory_WithEmoji_Success() {
        Long userId = 1L;
        CategoryRequest request = new CategoryRequest();
        request.setName("Food");
        request.setType(CategoryType.EXPENSE);
        request.setEmoji("\uD83C\uDF54"); // 🍔

        User user = User.builder().id(userId).build();
        CategoryIcon icon = CategoryIcon.builder().id(1L).name("Food").emoji("🍔").iconUrl("icon.png").build();
        Category category = Category.builder().id(10L).user(user).categoryIcon(icon).type(CategoryType.EXPENSE).build();
        CategoryResponse response = CategoryResponse.builder().id(10L).name("Food").type(CategoryType.EXPENSE).icon("🍔").iconUrl("icon.png").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryIconRepository.findByEmoji("🍔")).thenReturn(Optional.of(icon));
        when(categoryRepository.existsByCategoryIconNameIgnoreCaseAndUserId("Food", userId)).thenReturn(false);
        when(categoryMapper.toResponse(any())).thenReturn(response);

        CategoryResponse result = categoryService.addCategory(request, userId);

        assertEquals("Food", result.getName());
        assertEquals("🍔", result.getIcon());
        verify(categoryRepository).save(any());
    }

    @Test
    public void testAddCategory_DefaultIcon_WhenEmojiAndNameNotFound() {
        Long userId = 1L;
        CategoryRequest request = new CategoryRequest();
        request.setName("Unknown");
        request.setType(CategoryType.INCOME);

        User user = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryIconRepository.findByNameIgnoreCase("Unknown")).thenReturn(Optional.empty());
        when(categoryIconRepository.findByEmoji("👤")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.addCategory(request, userId);
        });
    }

    @Test
    public void testGetCategories_ByType_ReturnList() {
        Long userId = 1L;
        Category category = Category
                .builder()
                .id(1L)
                .type(CategoryType.EXPENSE)
                .categoryIcon(CategoryIcon
                        .builder()

                        .name("Food")
                        .emoji("🍔 ")
                        .iconUrl("icon.png")

                        .build())
                .build();
        CategoryResponse.Simple dto = CategoryResponse.Simple.builder().id(1L).name("Food").icon("🍔").iconUrl("icon.png").build();

        when(categoryRepository.findByUserIdAndType(userId, CategoryType.EXPENSE)).thenReturn(List.of(category));
        when(categoryMapper.toSimpleResponse(category)).thenReturn(dto);

        Map<String, List<CategoryResponse.Simple>> result = categoryService.getCategories(CategoryType.EXPENSE, userId);

        assertTrue(result.containsKey("data"));
        assertEquals(1, result.get("data").size());
        assertEquals("Food", result.get("data").get(0).getName());
    }

    @Test
    public void testGetCategories_ByType_Empty_ThrowsNotFound() {
        when(categoryRepository.findByUserIdAndType(1L, CategoryType.INCOME)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () ->
                categoryService.getCategories(CategoryType.INCOME, 1L));
    }



}

