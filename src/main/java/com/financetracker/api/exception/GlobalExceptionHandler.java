package com.financetracker.api.exception;

import com.financetracker.api.response.ErrorDomain;
import com.financetracker.api.response.ErrorResponse;
import com.financetracker.api.response.FieldErrorDTO;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.repository.config.RepositoryNameSpaceHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // handler Valid
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        List<FieldErrorDTO> errorList = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new FieldErrorDTO(err.getField(), err.getDefaultMessage()))
                .collect(Collectors.toList());

        ErrorResponse response = ErrorResponse.of("Validation failed", errorList);

        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }


}
