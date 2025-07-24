package com.financetracker.api.repository;

import com.financetracker.api.entity.Category;
import com.financetracker.api.entity.User;
import com.financetracker.api.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByIdAndUserId(Long id, Long userId);

    Optional<Category> findByIdAndUserAndType(Long id, User user , CategoryType type);
}
