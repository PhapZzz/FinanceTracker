package com.financetracker.api.exception;

import com.financetracker.api.response.ErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Getter
public class AppException extends RuntimeException {

    private final HttpStatus status;

    public AppException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST; // default
    }

    public AppException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(ex.getMessage(), null));
    }

}
