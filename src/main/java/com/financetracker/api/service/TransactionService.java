package com.financetracker.api.service;

import com.financetracker.api.entity.Category;
import com.financetracker.api.entity.Transaction;
import com.financetracker.api.entity.User;
import com.financetracker.api.enums.CategoryType;
import com.financetracker.api.exception.ResourceNotFoundException;
import com.financetracker.api.mapper.TransactionMapper;
import com.financetracker.api.repository.CategoryRepository;
import com.financetracker.api.repository.TransactionRepository;
import com.financetracker.api.repository.UserRepository;
import com.financetracker.api.request.TransactionRequest;
import com.financetracker.api.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;



@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;
    private final UserRepository userRepository;

    public TransactionResponse addTransaction(TransactionRequest request, Long userId) {
        // Load user từ DB theo userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Kiểm tra category có tồn tại và thuộc user
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Category does not belong to the current user");
        }

        // Parse ngày
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(request.getDate());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd");
        }



        // Tạo Transaction
        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .note(request.getNote())
                .category(category)
                .user(user)
                .type(CategoryType.valueOf(category.getType().name()))
                .date(parsedDate)
                .build();

        transactionRepository.save(transaction);
        return transactionMapper.toResponse(transaction);
    }
}