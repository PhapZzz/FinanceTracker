package com.financetracker.api.dto;

import com.financetracker.api.repository.SummaryRepository;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyReportDTO {
    private List<ChartItemDTO> charts;
    private ChartItemDTO summary;
}
