package com.financetracker.api.response;

import lombok.*;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ErrorResponse {
    private final String success = "false";
    private String message;
    private List<FieldErrorDTO> errors;


    public static ErrorResponse of(String message, List<FieldErrorDTO> errors) {
        ErrorResponse response = new ErrorResponse();
        response.message = message;
        response.errors = errors;
        return response;
    }
}
