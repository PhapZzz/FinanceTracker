package com.financetracker.api.response;


import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@JsonPropertyOrder({ "success","message", "data" }) // Ép thứ tự các trường
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class SuccessResponse<T> {
    private final String success = "success";
    private T data;
    private String message;

    public static <T> SuccessResponse<T> of(final T data, String message) {
        return SuccessResponse.<T>builder()
                .data(data)
                .message(message)
                .build();
    }



}

