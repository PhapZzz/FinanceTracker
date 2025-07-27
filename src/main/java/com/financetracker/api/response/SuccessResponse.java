package com.financetracker.api.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
@JsonPropertyOrder({ "success","message", "data" }) // Ép thứ tự các trường
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResponse<T> {

    private String success = "true";
    private T data;
    private String message;

    public static <T> SuccessResponse<T> of(final T data, String message) {
        return SuccessResponse.<T>builder()
                .success("true")
                .data(data)
                .message(message)

                .build();
    }



}

