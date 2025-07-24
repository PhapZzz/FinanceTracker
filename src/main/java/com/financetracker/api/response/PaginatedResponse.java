package com.financetracker.api.response;
//class phụ để đóng gói pagination đẹp

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PaginatedResponse<T> {

    private final List<T> data;
    private final Pagination pagination;

    public PaginatedResponse(Page<T> page) {
        this.data = page.getContent();
        this.pagination = new Pagination(page.getNumber() + 1, page.getTotalPages(), page.getTotalElements());
    }

    @Getter
    public static class Pagination {

        private final int currentPage;
        private final int totalPages;
        private final long totalItems;

        public Pagination(int currentPage, int totalPages, long totalItems) {
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.totalItems = totalItems;
        }
    }
}