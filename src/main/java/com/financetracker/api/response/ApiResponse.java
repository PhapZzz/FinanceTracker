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

    // ✅ Thành công có dữ liệu
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    // ✅ Thành công không có dữ liệu (ví dụ: danh sách trống)
    public static ApiResponse<Object> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    // ✅ Lỗi chung (dạng chuỗi)
    public static ApiResponse<Object> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    // ✅ Lỗi validation (dạng danh sách field/message)
    public static ApiResponse<List<ValidationError>> validationFailed(List<ValidationError> errors) {
        return new ApiResponse<>(false, "Validation failed", errors);
    }

    // ✅ Inner class cho lỗi validation
    @Getter
    @AllArgsConstructor
    public static class ValidationError {
        private String field;
        private String message;
    }
}