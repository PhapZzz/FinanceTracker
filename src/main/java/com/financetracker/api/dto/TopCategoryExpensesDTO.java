package com.financetracker.api.dto;
// tạo giả lập
public class TopCategoryExpensesDTO implements TopCategoryExpenses {
    private String category;
    private String icon;
    private double amount;

    public TopCategoryExpensesDTO(String category, String icon, double amount) {
        this.category = category;
        this.icon = icon;
        this.amount = amount;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public double getAmount() {
        return amount;
    }
}
