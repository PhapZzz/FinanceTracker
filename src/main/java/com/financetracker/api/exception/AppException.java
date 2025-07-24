package com.financetracker.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

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
//    @ExceptionHandler(AppException.class)
//    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
//        return ResponseEntity.status(HttpStatus.CONFLICT)
//                .body(ErrorResponse.of(ex.getMessage(), null));
//    }

}
