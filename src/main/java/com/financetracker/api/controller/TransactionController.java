package com.financetracker.api.controller;

import com.financetracker.api.AuthUtil;
import com.financetracker.api.enums.CategoryType;
import com.financetracker.api.dto.request.TransactionRequest;
import com.financetracker.api.dto.response.ApiResponse;
import com.financetracker.api.dto.response.TransactionHistoryResponse;
import com.financetracker.api.dto.response.TransactionResponse;
import com.financetracker.api.security.CustomUserDetails;
import com.financetracker.api.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Validated
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<?> addTransaction(@Valid @RequestBody TransactionRequest request,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        TransactionResponse response = transactionService
                .addTransaction(request, userDetails.getUser().getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Transaction added successfully", response)
        );
    }


    //phần còn lại của history
    @GetMapping("/history")
    public ApiResponse<?> getTransactionHistory(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) CategoryType type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = AuthUtil.getCurrentUserId();

        // Convert ngày nếu có
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;

        Page<TransactionHistoryResponse> result = transactionService.getTransactionHistory(
                userId,
                start,
                end,
                type,
                categoryId,
                page,
                size
        );

        return ApiResponse.success(
                "Transaction history fetched successfully",
                result.getContent(),
                new ApiResponse.Pagination(
                        result.getNumber() + 1,
                        result.getTotalPages(),
                        result.getTotalElements()
                )
        );
    }
}
/*return ApiResponse.success(
    "Transaction history fetched successfully",
    result.getContent(),
    new ApiResponse.Pagination(
        result.getNumber() + 1,
        result.getTotalPages(),
        result.getTotalElements()
    )
);*/