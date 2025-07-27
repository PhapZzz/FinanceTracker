package com.financetracker.api.dto;

import com.financetracker.api.enums.CategoryType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieChart {
    String category;
    Double amount;
    CategoryType type;

}
