package com.financetracker.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financetracker.api.dto.DashboardResponse;
import com.financetracker.api.response.SuccessResponse;
import com.financetracker.api.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService){
        this.dashboardService = dashboardService;

    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDashboardData(@RequestParam Integer month , @RequestParam Integer year){
        DashboardResponse result = dashboardService.getDashboard(month, year);
        System.out.println(">>> Dashboard data: " + result);
        SuccessResponse<DashboardResponse> response = SuccessResponse.of(result, "ok");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/test")
    public ResponseEntity<?> testJson() {
        return ResponseEntity.ok(SuccessResponse.of("test-data", "ok"));
    }

}
