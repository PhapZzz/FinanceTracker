package com.financetracker.api.service;

import com.financetracker.api.entity.Budget;
import com.financetracker.api.entity.Category;
import com.financetracker.api.entity.User;
import com.financetracker.api.enums.CategoryType;
import com.financetracker.api.exception.ResourceNotFoundException;
import com.financetracker.api.mapper.BudgetMapper;
import com.financetracker.api.repository.BudgetRepository;
import com.financetracker.api.repository.CategoryRepository;
import com.financetracker.api.repository.UserRepository;
import com.financetracker.api.dto.request.BudgetRequest;
import com.financetracker.api.dto.request.UpdateBudgetRequest;
import com.financetracker.api.dto.response.BudgetResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {


    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final BudgetMapper budgetMapper;

    /**
     * Thêm hoặc cập nhật ngân sách theo category + month + year
     */
    @Transactional
    public BudgetResponse addOrUpdateBudget(BudgetRequest request, Long userId) {

        //tìm userid có tồn tại
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        //tìm category có tồn tại
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        //so sánh category coi có dính usser
        if (!category.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Category does not belong to the current user");
        }
        //coi category và icon dính nhau
        if (category.getType() != CategoryType.EXPENSE) {
            throw new IllegalArgumentException("Only EXPENSE categories can have budgets");
        }

        // Nếu đã tồn tại ngân sách → cập nhật
        Budget budget = budgetRepository
                .findByCategoryIdAndUserIdAndMonthAndYear(request.getCategoryId(), userId, request.getMonth(), request.getYear())
                .map(
                        existing ->
                        {
                            existing.setAmount(request.getAmount());
                            return existing;
                        }
                )
                .orElseGet(() -> Budget
                        .builder()

                        .category(category)
                        .user(user)
                        .amount(request.getAmount())
                        .month(request.getMonth())
                        .year(request.getYear())

                        .build()
                );

        budgetRepository.save(budget);
        return budgetMapper.toResponse(budget);
    }

    /**
     * Lấy danh sách ngân sách theo tháng/năm của user
     */
    public List<BudgetResponse> getBudgetsByMonthAndYear(Long userId, int month, int year) {
        return budgetRepository
                .findAllByUserIdAndMonthAndYear(userId, month, year)
                .stream()
                .map(budgetMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật số tiền của ngân sách
     */
    @Transactional
    public BudgetResponse updateBudget(Long budgetId, UpdateBudgetRequest request, Long userId) {

        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        budget.setAmount(request.getAmount());

        budgetRepository.save(budget);
        return budgetMapper.toResponse(budget);
    }

    /**
     * Xoá ngân sách theo ID
     */
    @Transactional
    public void deleteBudget(Long budgetId, Long userId) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        budgetRepository.delete(budget);
    }
}
