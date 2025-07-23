package com.financetracker.api.repository;

import com.financetracker.api.entity.Category;
import com.financetracker.api.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameAndUserId(String name, Long userId);

    List<Category> findByUserIdAndType(Long userId, CategoryType type);

    List<Category> findByUserId(Long userId);
}