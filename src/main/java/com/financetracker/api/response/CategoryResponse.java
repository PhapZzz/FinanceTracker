package com.financetracker.api.response;

import com.financetracker.api.enums.CategoryType;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private CategoryType type;
    private String icon;
    private String iconUrl;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Simple {
        private Long id;
        private String name;
        private String icon;
        private String iconUrl;
    }
}
