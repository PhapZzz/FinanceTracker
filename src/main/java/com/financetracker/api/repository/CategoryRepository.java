package com.financetracker.api.repository;

import com.financetracker.api.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

}
