package com.financetracker.api.service;

import com.financetracker.api.entity.*;
import com.financetracker.api.enums.CategoryType;
import com.financetracker.api.exception.ResourceNotFoundException;
import com.financetracker.api.mapper.TransactionMapper;
import com.financetracker.api.repository.CategoryRepository;
import com.financetracker.api.repository.TransactionRepository;
import com.financetracker.api.repository.UserRepository;
import com.financetracker.api.dto.request.TransactionRequest;
import com.financetracker.api.dto.response.TransactionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private UserRepository userRepository;
    @Mock private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Add transaction - success")
    void testAddTransactionSuccess() {
        Long userId = 1L;
        Long categoryId = 2L;

        TransactionRequest request = new TransactionRequest();
        request.setAmount(BigDecimal.valueOf(100));
        request.setNote("Lunch");
        request.setDate("2025-07-27");
        request.setCategoryId(categoryId);

        User user = User.builder().id(userId).build();
        CategoryIcon icon = CategoryIcon.builder().name("Food").emoji("🍔").iconUrl("url").build();
        Category category = Category.builder().id(categoryId).user(user).categoryIcon(icon).type(CategoryType.EXPENSE).build();
        Transaction transaction = Transaction.builder().id(10L).user(user).category(category).amount(BigDecimal.valueOf(100)).date(LocalDate.parse("2025-07-27")).build();

        TransactionResponse expectedResponse = TransactionResponse.builder()
                .id(10L)
                .amount(BigDecimal.valueOf(100))
                .date(LocalDate.parse("2025-07-27"))
                .note("Lunch")
                .category(TransactionResponse.CategoryDTO.builder()
                        .id(categoryId)
                        .name("Food")
                        .type("EXPENSE")
                        .emoji("🍔")
                        .iconUrl("url")
                        .build())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any())).thenReturn(transaction);
        when(transactionMapper.toResponse(any())).thenReturn(expectedResponse);

        TransactionResponse result = transactionService.addTransaction(request, userId);

        assertEquals(expectedResponse.getId(), result.getId());
        assertEquals(expectedResponse.getAmount(), result.getAmount());
        verify(transactionRepository).save(any());
    }

    @Test
    @DisplayName("Add transaction - user not found")
    void testAddTransactionUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        TransactionRequest request = new TransactionRequest();
        request.setCategoryId(2L);
        request.setDate("2025-07-27");
        request.setAmount(BigDecimal.TEN);

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.addTransaction(request, 1L));
    }

    @Test
    @DisplayName("Add transaction - category not found")
    void testAddTransactionCategoryNotFound() {
        User user = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        TransactionRequest request = new TransactionRequest();
        request.setCategoryId(2L);
        request.setDate("2025-07-27");
        request.setAmount(BigDecimal.TEN);

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.addTransaction(request, 1L));
    }

    @Test
    @DisplayName("Add transaction - category not belong to user")
    void testAddTransactionCategoryNotBelongToUser() {
        User user = User.builder().id(1L).build();
        User otherUser = User.builder().id(2L).build();
        Category category = Category.builder().id(2L).user(otherUser).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

        TransactionRequest request = new TransactionRequest();
        request.setCategoryId(2L);
        request.setDate("2025-07-27");
        request.setAmount(BigDecimal.TEN);

        assertThrows(AccessDeniedException.class,
                () -> transactionService.addTransaction(request, 1L));
    }

    @Test
    @DisplayName("Add transaction - invalid date format")
    void testAddTransactionInvalidDate() {
        User user = User.builder().id(1L).build();
        Category category = Category.builder().id(2L).user(user).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

        TransactionRequest request = new TransactionRequest();
        request.setCategoryId(2L);
        request.setDate("invalid-date");
        request.setAmount(BigDecimal.TEN);

        assertThrows(IllegalArgumentException.class,
                () -> transactionService.addTransaction(request, 1L));
    }
}
