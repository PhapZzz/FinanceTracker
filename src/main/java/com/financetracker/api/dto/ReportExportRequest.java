package com.financetracker.api.dto;

import com.financetracker.api.enums.ReportPDFtype;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportExportRequest {
    @NotNull(message = "Month must not be null")
    private Integer month;

    @NotNull(message = "Year must not be null")
    private Integer year;

    private Boolean includeChart = true;

    private Boolean includeTopExpenses = true;

    @NotNull(message = "Report type must not be null")
    @Enumerated(EnumType.STRING)
    private ReportPDFtype reportType;

}
