package com.financetracker.api.service;

import com.financetracker.api.entity.Budget;
import com.financetracker.api.entity.Category;
import com.financetracker.api.entity.CategoryIcon;
import com.financetracker.api.entity.User;
import com.financetracker.api.enums.CategoryType;
import com.financetracker.api.mapper.BudgetMapper;
import com.financetracker.api.repository.BudgetRepository;
import com.financetracker.api.repository.CategoryRepository;
import com.financetracker.api.repository.UserRepository;
import com.financetracker.api.request.BudgetRequest;
import com.financetracker.api.request.UpdateBudgetRequest;
import com.financetracker.api.response.BudgetResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BudgetServiceTest {

    @Mock private BudgetRepository budgetRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private UserRepository userRepository;
    @Mock private BudgetMapper budgetMapper;

    @InjectMocks private BudgetService budgetService;

    private User user;
    private Category category;
    private CategoryIcon icon;
    private Budget budget;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);

        icon = new CategoryIcon();
        icon.setName("Food");
        icon.setEmoji("🍽️");
        icon.setIconUrl("https://cdn.example.com/food.png");

        category = new Category();
        category.setId(2L);
        category.setUser(user);
        category.setType(CategoryType.EXPENSE);
        category.setCategoryIcon(icon);

        budget = Budget.builder()
                .id(10L)
                .user(user)
                .category(category)
                .amount(new BigDecimal("500.00"))
                .month(7)
                .year(2025)
                .build();
    }

    @Test
    void addOrUpdateBudget_shouldCreateNewBudget() {
        BudgetRequest request = new BudgetRequest();
        request.setAmount(new BigDecimal("500.00"));
        request.setMonth(7);
        request.setYear(2025);
        request.setCategoryId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(budgetRepository.findByCategoryIdAndUserIdAndMonthAndYear(2L, 1L, 7, 2025)).thenReturn(Optional.empty());
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);
        when(budgetMapper.toResponse(any())).thenReturn(BudgetResponse.builder().id(10L).build());

        BudgetResponse response = budgetService.addOrUpdateBudget(request, 1L);
        assertNotNull(response);
        assertEquals(10L, response.getId());
    }

    @Test
    void addOrUpdateBudget_shouldUpdateExistingBudget() {
        BudgetRequest request = new BudgetRequest();
        request.setAmount(new BigDecimal("999.99"));
        request.setMonth(7);
        request.setYear(2025);
        request.setCategoryId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(budgetRepository.findByCategoryIdAndUserIdAndMonthAndYear(2L, 1L, 7, 2025)).thenReturn(Optional.of(budget));
        when(budgetRepository.save(any())).thenReturn(budget);
        when(budgetMapper.toResponse(any())).thenReturn(BudgetResponse.builder().id(10L).build());

        BudgetResponse response = budgetService.addOrUpdateBudget(request, 1L);
        assertNotNull(response);
        verify(budgetRepository).save(budget);
    }

    @Test
    void getBudgetsByMonthAndYear_shouldReturnList() {
        when(budgetRepository.findAllByUserIdAndMonthAndYear(1L, 7, 2025))
                .thenReturn(List.of(budget));
        when(budgetMapper.toResponse(budget)).thenReturn(BudgetResponse.builder().id(10L).build());

        List<BudgetResponse> responses = budgetService.getBudgetsByMonthAndYear(1L, 7, 2025);
        assertEquals(1, responses.size());
        assertEquals(10L, responses.get(0).getId());
    }

    @Test
    void updateBudget_shouldUpdateAmount() {
        UpdateBudgetRequest request = new UpdateBudgetRequest();
        request.setAmount(new BigDecimal("123.45"));

        when(budgetRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(budget));
        when(budgetRepository.save(any())).thenReturn(budget);
        when(budgetMapper.toResponse(budget)).thenReturn(BudgetResponse.builder().id(10L).build());

        BudgetResponse response = budgetService.updateBudget(10L, request, 1L);
        assertEquals(10L, response.getId());
        assertEquals(new BigDecimal("123.45"), budget.getAmount());
    }

    @Test
    void deleteBudget_shouldRemoveIfExists() {
        when(budgetRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(budget));

        budgetService.deleteBudget(10L, 1L);
        verify(budgetRepository).delete(budget);
    }

    @Test
    void addOrUpdateBudget_shouldThrowIfCategoryNotBelongUser() {
        User anotherUser = new User();
        anotherUser.setId(99L);
        category.setUser(anotherUser);

        BudgetRequest request = new BudgetRequest();
        request.setAmount(new BigDecimal("500.00"));
        request.setMonth(7);
        request.setYear(2025);
        request.setCategoryId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

        assertThrows(AccessDeniedException.class, () -> budgetService.addOrUpdateBudget(request, 1L));
    }
}
