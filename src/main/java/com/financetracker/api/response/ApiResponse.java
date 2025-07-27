package com.financetracker.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of") // Optional factory method
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;


    //tách tránh lồng data
//    private PaginatedResponse paginatedResponse;


    // Thành công có dữ liệu
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    // Thành công không có dữ liệu (ví dụ: danh sách trống)
    public static ApiResponse<Object> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    // Lỗi chung (dạng chuỗi)
    public static ApiResponse<Object> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    // Lỗi validation (dạng danh sách field/message)
    public static ApiResponse<List<ValidationError>> validationFailed(List<ValidationError> errors) {
        return new ApiResponse<>(false, "Validation failed", errors);
    }

    //  Inner class cho lỗi validation
    @Getter
    @AllArgsConstructor
    public static class ValidationError {
        private String field;
        private String message;
    }

}
/*
* @Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Pagination pagination; // 👈 thêm phần này nếu cần

    // ✅ Cho response có phân trang
    public static <T> ApiResponse<T> success(String message, T data, Pagination pagination) {
        return new ApiResponse<>(true, message, data, pagination);
    }

    // ✅ Response không phân trang (giữ nguyên)
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static ApiResponse<Object> success(String message) {
        return new ApiResponse<>(true, message, null, null);
    }

    public static ApiResponse<Object> error(String message) {
        return new ApiResponse<>(false, message, null, null);
    }

    public static ApiResponse<List<ValidationError>> validationFailed(List<ValidationError> errors) {
        return new ApiResponse<>(false, "Validation failed", errors, null);
    }

    @Getter
    @AllArgsConstructor
    public static class ValidationError {
        private String field;
        private String message;
    }

    @Getter
    @AllArgsConstructor
    public static class Pagination {
        private int currentPage;
        private int totalPages;
        private long totalItems;
    }
}

* */
