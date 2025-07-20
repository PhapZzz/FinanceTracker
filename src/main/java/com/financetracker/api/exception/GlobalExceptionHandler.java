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
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

    // Xử lý lỗi @Valid
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

        return new ResponseEntity<>(response, status);
    }

    // Xử lý lỗi AppException do dev chủ động throw ra
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
        ErrorResponse response = ErrorResponse.of(ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Xử lý lỗi Token hết hạn
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpiredException(TokenExpiredException ex) {
        ErrorResponse response = ErrorResponse.of("Token expired", null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // Xử lý lỗi Token không hợp lệ
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException ex) {
        ErrorResponse response = ErrorResponse.of("Invalid token", null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // Xử lý fallback lỗi khác chưa được bắt
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        ex.printStackTrace(); // Log lỗi
        ErrorResponse response = ErrorResponse.of("Internal server error", null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


