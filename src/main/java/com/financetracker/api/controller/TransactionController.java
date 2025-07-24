package com.financetracker.api.controller;

import com.financetracker.api.request.TransactionRequest;
import com.financetracker.api.response.ApiResponse;
import com.financetracker.api.response.TransactionResponse;
import com.financetracker.api.security.CustomUserDetails;
import com.financetracker.api.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Validated
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<?> addTransaction(@Valid @RequestBody TransactionRequest request,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        TransactionResponse response = transactionService.addTransaction(request, userDetails.getUser().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Transaction added successfully", response)
        );
    }
}
