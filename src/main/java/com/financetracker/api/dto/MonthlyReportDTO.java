package com.financetracker.api.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyReportDTO {
    private List<ChartItemDTO> charts;
    private ChartItemResponseDTO summary;
}
