package com.financetracker.api.service;

import com.financetracker.api.dto.MonthlyReportDTO;
import com.financetracker.api.dto.ReportExportRequest;
import com.financetracker.api.dto.SummaryReportDTO;

public interface ReportService {

    public MonthlyReportDTO getMonthlyReportbyYear( int year ,int month);
    public SummaryReportDTO getMonthlyReportAndTop3Expenses(int year , int monthtxt);
    String exportPDF(ReportExportRequest request);
}
