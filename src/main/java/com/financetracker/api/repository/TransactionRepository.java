package com.financetracker.api.repository;

import com.financetracker.api.entity.Transaction;
import com.financetracker.api.enums.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
        SELECT t FROM Transaction t
        WHERE t.user.id = :userId
          AND (:startDate IS NULL OR t.date >= :startDate)
          AND (:endDate IS NULL OR t.date <= :endDate)
          AND (:type IS NULL OR t.type = :type)
          AND (:categoryId IS NULL OR t.category.id = :categoryId)
        ORDER BY t.date DESC
    """)
    Page<Transaction> findHistoryByFilters(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") CategoryType type,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );
}
