package com.financetracker.api.mapper;

import com.financetracker.api.entity.Category;
import com.financetracker.api.entity.CategoryIcon;
import com.financetracker.api.entity.Transaction;
import com.financetracker.api.response.TransactionResponse;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(Transaction transaction) {
        Category category = transaction.getCategory();
        CategoryIcon icon = category.getCategoryIcon(); // lấy icon

        return TransactionResponse
                .builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .note(transaction.getNote())
                .date(transaction.getDate())
                .category(TransactionResponse.CategoryDTO
                        .builder()
                        .id(category.getId())
                        .name(icon.getName())
                        .type(icon.getType().name())
                        .emoji(icon.getEmoji())
                        .iconUrl(icon.getIconUrl())
                        .build())
                .build();
    }
}
