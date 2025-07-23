package com.financetracker.api.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartItemDTO {
    private String month;
    private Double income;
    private Double expense;

//    public String getMonthName() {
//        return Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase();
//    }
}
