package com.financetracker.api.service;

import com.financetracker.api.dto.DashboardResponse;

public interface DashboardService {

    DashboardResponse getDashboard(Integer month, Integer year);
}
