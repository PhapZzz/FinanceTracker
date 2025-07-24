package com.financetracker.api.mapper;

import com.financetracker.api.entity.Category;
import com.financetracker.api.entity.Transaction;
import com.financetracker.api.response.TransactionResponse;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(Transaction transaction) {
        Category category = transaction.getCategory();

        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .note(transaction.getNote())
                .date(transaction.getDate())
                .category(TransactionResponse.CategoryDTO.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .type(category.getType().name())
                        .icon(category.getEmoji())
                        .iconUrl(category.getIconUrl())
                        .build())
                .build();
    }
}
