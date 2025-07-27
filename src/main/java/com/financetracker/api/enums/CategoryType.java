package com.financetracker.api.enums;


import com.fasterxml.jackson.annotation.JsonCreator;

public enum CategoryType {
    INCOME,
    EXPENSE;


    @JsonCreator
    public static CategoryType from(String value) {
        try {
            return CategoryType.valueOf(value.toUpperCase());
        } catch (Exception e) {
            return null; // Trả về null để trigger @NotNull trong validation
        }
    }
    }