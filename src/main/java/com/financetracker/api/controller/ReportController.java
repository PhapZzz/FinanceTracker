package com.financetracker.api.controller;

import com.financetracker.api.dto.MonthlyReportDTO;
import com.financetracker.api.dto.ReportExportRequest;
import com.financetracker.api.dto.SummaryReportDTO;
import com.financetracker.api.response.SuccessResponse;
import com.financetracker.api.service.serviceImpl.ReportServiceImpl;
import io.jsonwebtoken.io.IOException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadReport(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get("uploads/reports").resolve(filename).normalize();

        if (!Files.exists(filePath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // hoặc trả message lỗi nếu muốn
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
