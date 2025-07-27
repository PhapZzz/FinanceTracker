package com.financetracker.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.financetracker.api.service.DashboardService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardResponse {
    private Double income;
    private Double expense;
    private Double balance;

    private List<PieChart> pieChart;
    private List<RecentTransactionsDTO> recentTransactions;


}
