package com.financetracker.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SummaryReportDTO {
    private String month;
    private Integer year;
    private Double income;
    private Double expense;
    private Double balance;
    private List<TopCategoryExpenses> topExpenses;

    public void setBalance(){
        this.balance = (income != null ? income : 0.0) - (expense != null ? expense : 0.0);
    }
    public SummaryReportDTO(String month, Integer year, Double income, Double expense, List<TopCategoryExpenses> topCategoryExpens){
        this.month = month;
        this.year = year;
        this.income = income;
        this.expense = expense;
        this.setBalance();
        this.topExpenses = topCategoryExpens;
    }

}
