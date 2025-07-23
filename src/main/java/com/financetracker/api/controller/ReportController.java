package com.financetracker.api.controller;

import com.financetracker.api.dto.MonthlyReportDTO;
import com.financetracker.api.dto.ReportExportRequest;
import com.financetracker.api.dto.SummaryReportDTO;
import com.financetracker.api.response.SuccessResponse;
import com.financetracker.api.service.serviceImpl.ReportServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private ReportServiceImpl reportService;

    @Autowired
    public ReportController (ReportServiceImpl reportService){
        this.reportService = reportService;
    }

    @GetMapping("/monthly")
    public ResponseEntity<SuccessResponse<MonthlyReportDTO>> getMonthlyReport(@RequestParam (value = "year") int year,
                                                            @RequestParam (value = "month") int monthtxt){
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.of(reportService.getMonthlyReportbyYear(year,monthtxt),"Monthly financial report fetched successfully"));
    }

    @GetMapping("/summary")
    public ResponseEntity<SuccessResponse<SummaryReportDTO>> getSummaryReport(@RequestParam (value = "year") int year,
                                                                              @RequestParam (value = "month") int monthtxt){
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.of(reportService.getMonthlyReportAndTop3Expenses(year,monthtxt),"Monthly summary fetched successfully"));
    }

    @PostMapping("/export/pdf")
    public ResponseEntity<?> getSummaryReport(@Valid @RequestBody ReportExportRequest exportRequest){
        String downloadUrl = reportService.exportPDF(exportRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Summary report PDF generated successfully");
        response.put("downloadUrl", downloadUrl);

        return ResponseEntity.ok(response);
    }

}
