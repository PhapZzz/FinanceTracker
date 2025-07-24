package com.financetracker.api.dto;

import com.financetracker.api.enums.ReportPDFtype;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportExportRequest {
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    @NotNull(message = "Month must not be null")
    private Integer month;

    @Min(value = 2000, message = "Year must be at least 2000")
    @NotNull(message = "Year must not be null")
    private Integer year;

    private Boolean includeChart = true;

    private Boolean includeTopExpenses = true;

    @NotNull(message = "Report type must not be null")
    @Enumerated(EnumType.STRING)
    private ReportPDFtype reportType;

}
