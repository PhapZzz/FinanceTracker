package com.financetracker.api.mapper;

import com.financetracker.api.entity.Budget;
import com.financetracker.api.entity.CategoryIcon;
import com.financetracker.api.response.BudgetResponse;
import org.springframework.stereotype.Component;

@Component
public class BudgetMapper {

    public BudgetResponse toResponse(Budget budget) {

        CategoryIcon icon = budget.getCategory().getCategoryIcon();

        return BudgetResponse
                .builder()

                .id(budget.getId())
                .amount(budget.getAmount())
                .month(budget.getMonth())
                .year(budget.getYear())

                .category(BudgetResponse.CategoryDTO
                        .builder()

                        .id(budget.getCategory().getId())
                        .name(icon.getName())
                        .emoji(icon.getEmoji())
                        .iconUrl(icon.getIconUrl())
                        .type(icon.getType().name())

                        .build())
                .build();
    }
}
