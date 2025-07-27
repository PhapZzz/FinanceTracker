package com.financetracker.api.controller;

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
    public ResponseEntity<SuccessResponse<DashboardResponse>> add(@RequestParam Integer month , @RequestParam Integer year){
        DashboardResponse result = dashboardService.getDashboard(month, year);
        System.out.println(">>> Dashboard data: " + result);
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.of(result,"ok"));

    }

}
