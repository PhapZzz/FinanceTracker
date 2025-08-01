package com.financetracker.api.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.financetracker.api.dto.FieldErrorDTO;
import com.financetracker.api.dto.response.ApiResponse;
import com.financetracker.api.dto.response.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    // ✅ Xử lý lỗi @Valid thông qua override thay vì @ExceptionHandler
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        List<FieldErrorDTO> errorList = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new FieldErrorDTO
                        (
                                err.getField(),
                                err.getDefaultMessage())
                )
                .distinct()
                .collect(Collectors.toList());

        ErrorResponse response = ErrorResponse.of("Validation failed", errorList);
        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    // ❗ Xử lý lỗi do dev chủ động ném ra
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
        HttpStatus status = ex.getStatus() != null
                ? ex.getStatus()
                : HttpStatus.UNAUTHORIZED;

        ErrorResponse response = ErrorResponse.of(ex.getMessage(), null);
        return new ResponseEntity<>(response, status);
    }

    // Xử lý lỗi AppException do dev chủ động throw ra
//    @ExceptionHandler(AppException.class)
//    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
//        ErrorResponse response = ErrorResponse.of(ex.getMessage(), null);
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//    @ExceptionHandler(AppException.class)
//    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
//        return ResponseEntity
//                .status(HttpStatus.UNAUTHORIZED) // 401
//                .body(new ErrorResponse(false, ex.getMessage()));
//    }

    // ❗ Xử lý lỗi token hết hạn
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpiredException(TokenExpiredException ex) {
        ErrorResponse response = ErrorResponse.of("Token expired", null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // ❗ Xử lý lỗi token không hợp lệ
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException ex) {
        ErrorResponse response = ErrorResponse.of("Invalid token", null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // ❗ Fallback cho các lỗi chưa xác định khác (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        ex.printStackTrace(); // Log lỗi để debug
        ErrorResponse response = ErrorResponse.of("Internal server error", null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Object> handleOtherExceptions(Exception ex) {
//        Map<String, Object> body = new HashMap<>();
//        body.put("success", false);
//        body.put("message", "Internal Server Error");
//        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("success", false, "message", ex.getMessage())
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException formatException) {
            Class<?> targetType = formatException.getTargetType();
            if (targetType.isEnum()) {
                Object[] constants = targetType.getEnumConstants();
                String allowedValues = Arrays.stream(constants)
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));

                String fieldName = formatException.getPath().stream()
                        .map(JsonMappingException.Reference::getFieldName)
                        .collect(Collectors.joining("."));

                Map<String, String> body = Map.of(
                        "error", "Invalid value for enum field: " + fieldName,
                        "allowed", allowedValues
                );

                return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
            }

        }

        // fallback xử lý mặc định
        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    //phần xử lý này của budgetservice
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(403).body(
                ApiResponse.error("Access denied: " + ex.getMessage())
        );
    }

    // ✅ Dành cho lỗi 409 Conflict (trùng tên danh mục)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleConflict(IllegalStateException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                        "success", false,
                        "message", ex.getMessage()
                )
        );
    }



}
