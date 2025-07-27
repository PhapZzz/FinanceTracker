package com.financetracker.api.repository;

import com.financetracker.api.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByCategoryIdAndUserIdAndMonthAndYear(Long categoryId, Long userId, int month, int year);

    List<Budget> findAllByUserIdAndMonthAndYear(Long userId, int month, int year);

    Optional<Budget> findByIdAndUserId(Long budgetId, Long userId);
}
