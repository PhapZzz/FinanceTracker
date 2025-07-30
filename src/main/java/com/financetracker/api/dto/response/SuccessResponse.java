package com.financetracker.api.dto.response;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
@JsonPropertyOrder({ "success","message", "data" }) // Ép thứ tự các trường
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class SuccessResponse<T> {

    private final String success = "true";
    private T data;
    private String message;

    public static <T> SuccessResponse<T> of(final T data, String message) {
        return SuccessResponse.<T>builder()

                .data(data)
                .message(message)

                .build();
    }



}

