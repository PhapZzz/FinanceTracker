package com.financetracker.api.repository;

import com.financetracker.api.entity.Category;
import com.financetracker.api.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndUserId(Long id, Long userId);

    @Query("""
        SELECT COUNT(c) > 0
        FROM Category c
        WHERE LOWER(c.categoryIcon.name) = LOWER(:name)
          AND c.user.id = :userId
    """)
    boolean existsByCategoryIconNameIgnoreCaseAndUserId(String name, Long userId);

    //  c.type (không phải c.category.type)
    @Query("""
        SELECT c FROM Category c
        WHERE c.user.id = :userId
          AND c.type = :type
    """)
    List<Category> findByUserIdAndType(Long userId, CategoryType type);

    @Query("""
        SELECT c FROM Category c
        WHERE c.user.id = :userId
    """)
    List<Category> findByUserId(Long userId);
}
