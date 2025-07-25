package com.financetracker.api.response;

import com.financetracker.api.enums.CategoryType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private CategoryType type;
    private String icon;
    private String iconUrl;

    @Getter
    @Setter
    @Builder
    public static class Simple {
        private Long id;
        private String name;
        private String icon;
        private String iconUrl;
    }
}
