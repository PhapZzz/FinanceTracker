package com.financetracker.api.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "success","message", "data" }) // Đảm bảo thứ tự
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SuccessResponse<T> {
    private Boolean success = true;
    private T data;
    private String message;

    public static <T> SuccessResponse<T> of(final T data, String message) {
        return SuccessResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .build();
    }
}

