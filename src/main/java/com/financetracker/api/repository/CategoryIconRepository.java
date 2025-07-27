package com.financetracker.api.repository;

import com.financetracker.api.entity.CategoryIcon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryIconRepository extends JpaRepository<CategoryIcon, Long> {

    // ✅ Tìm theo name (ignore case) và type
//    Optional<CategoryIcon> findByNameIgnoreCaseAndType(String name, CategoryType type);
//
//    // ✅ Tìm theo emoji và type (Spring hiểu được)
//    Optional<CategoryIcon> findByEmojiAndType(String emoji, CategoryType type);
//
//    boolean existsByNameIgnoreCase(String name);


    Optional<CategoryIcon> findByNameIgnoreCase(String name);

    Optional<CategoryIcon> findByEmoji(String emoji);

    boolean existsByNameIgnoreCase(String name);

}

