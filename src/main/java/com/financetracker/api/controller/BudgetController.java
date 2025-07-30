package com.financetracker.api.controller;

import com.financetracker.api.dto.request.BudgetRequest;
import com.financetracker.api.dto.request.UpdateBudgetRequest;
import com.financetracker.api.dto.response.ApiResponse;
import com.financetracker.api.dto.response.BudgetResponse;
import com.financetracker.api.security.CustomUserDetails;
import com.financetracker.api.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    /**
     * POST /api/budgets
     * Thêm hoặc cập nhật ngân sách
     */
    @PostMapping
    public ResponseEntity<?> addOrUpdateBudget(@Valid @RequestBody BudgetRequest request,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        BudgetResponse response = budgetService.addOrUpdateBudget(request, userDetails.getUser().getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Budget saved successfully", response)
                );
    }

    /**
     * GET /api/budgets?month=7&year=2025
     * Lấy danh sách ngân sách theo tháng/năm
     */
    @GetMapping
    public ResponseEntity<?> getBudgetsByMonthAndYear(@RequestParam int month,
                                                      @RequestParam int year,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<BudgetResponse> budgets = budgetService.getBudgetsByMonthAndYear(userDetails.getUser().getId(), month, year);
        return ResponseEntity
                .ok(ApiResponse.success("Budget list fetched successfully", budgets)
                );
    }

    /**
     * PUT /api/budgets/{id}
     * Cập nhật số tiền ngân sách
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBudget(@PathVariable Long id,
                                          @Valid @RequestBody UpdateBudgetRequest request,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        BudgetResponse response = budgetService.updateBudget(id, request, userDetails.getUser().getId());
        return ResponseEntity
                .ok(ApiResponse.success("Budget updated successfully", response)
                );
    }

    /**
     * DELETE /api/budgets/{id}
     * Xoá ngân sách
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(@PathVariable Long id,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {

        budgetService.deleteBudget(id, userDetails.getUser().getId());

        return ResponseEntity
                .noContent()
                .build(); // 204 No Content
    }
}
