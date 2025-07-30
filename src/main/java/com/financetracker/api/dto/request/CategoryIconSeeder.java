package com.financetracker.api.dto.request;

import com.financetracker.api.entity.CategoryIcon;
import com.financetracker.api.repository.CategoryIconRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryIconSeeder {

    private final CategoryIconRepository categoryIconRepository;

    @PostConstruct
    public void seedIcons() {
        if (categoryIconRepository.count() > 0) return; // tránh chèn trùng

        List<CategoryIcon> icons = List.of(
                new CategoryIcon(null, "Housing", "🏠", "https://cdn.example.com/icons/housing.png"),
                new CategoryIcon(null, "Food", "🍽", "https://cdn.example.com/icons/food.png"),
                new CategoryIcon(null, "Shopping", "🛍", "https://cdn.example.com/icons/shopping.png"),
                new CategoryIcon(null, "Salary", "💵", "https://cdn.example.com/icons/salary.png"),
                new CategoryIcon(null, "Freelance", "💼", "https://cdn.example.com/icons/freelance.png"),
                new CategoryIcon(null, "Investments", "📈", "https://cdn.example.com/icons/investments.png"),
                new CategoryIcon(null, "Other", "👤", "https://cdn.example.com/icons/other.png"),
                new CategoryIcon(null, "Transportation", "🚗", "https://cdn.example.com/icons/transportation.png"),
                new CategoryIcon(null, "Entertainment", "🎮", "https://cdn.example.com/icons/entertainment.png"),
                new CategoryIcon(null, "Health", "🏥", "https://cdn.example.com/icons/health.png"),
                new CategoryIcon(null, "Education", "🎓", "https://cdn.example.com/icons/education.png"),
                new CategoryIcon(null, "Gifts", "🎁", "https://cdn.example.com/icons/gifts.png")
        );

        categoryIconRepository.saveAll(icons);
    }
}
