package com.financetracker.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.financetracker.api.dto.FieldErrorDTO;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) 
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
