package com.financetracker.api.service.serviceImpl;

import com.financetracker.api.dto.*;
import com.financetracker.api.enums.CategoryType;
import com.financetracker.api.repository.SummaryRepository;
import com.financetracker.api.security.Jwt.util.JwtTokenUtil;
import com.financetracker.api.service.DashboardService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final SummaryRepository summaryRepository;

    @Autowired
    public DashboardServiceImpl(SummaryRepository summaryRepository) {
        this.summaryRepository = summaryRepository;
    }

    public static long getUserID() {
//        return JwtTokenUtil.getCurrentUserId();
        return 1;

    }

    // hàm chuyển tháng số sang tháng chữ
    public static String getMonthName(int monthNum) {
        String monthStr = monthNum > 0 && monthNum <= 12
                ? Month.of(monthNum).getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase()
                : "N/A";
        return monthStr;
    }

    @Override
    public DashboardResponse getDashboard(Integer month, Integer year) {
        Long userId = getUserID();


        List<ChartItemDTO> chartItems = summaryRepository.getMonthlyReport(userId, year).stream()
                .map(cols -> {

                    int monthNum = cols[0] != null ? ((Number) cols[0]).intValue() : 0;
                    Double income = cols[1] != null ? ((Number) cols[1]).doubleValue() : 0.0;
                    Double expense = cols[2] != null ? ((Number) cols[2]).doubleValue() : 0.0;

                    return new ChartItemDTO(getMonthName(monthNum), income, expense);
                })
                .toList();

        ChartItemResponseDTO chartItemDTORepon = chartItems.stream()
                .filter(item -> item.getMonth().equals(getMonthName(month)))
                .findFirst()
                .map(item -> new ChartItemResponseDTO(item.getMonth(), item.getIncome(), item.getExpense()))
                .orElse(new ChartItemResponseDTO(getMonthName(month), 0.0, 0.0)); // fallback nếu không có dữ liệu

        List<Object[]> results = summaryRepository.getCategory(userId, month, year);

        List<PieChart> charts = new ArrayList<>();
        List<RecentTransactionsDTO> dtos = new ArrayList<>();

        for (Object[] row : results) {
            BigDecimal amountBD = new BigDecimal(row[0].toString());
            double amount = amountBD.doubleValue();
            Long category_ID = (Long) row[1];
            String category = (String) row[2];
            String typeStr = (String) row[3];
            java.sql.Date sqlDate = (java.sql.Date) row[4];
            String emoji = (String) row[5];

            dtos.add(RecentTransactionsDTO.builder()
                    .id(category_ID)
                    .amount(amountBD)
                    .category(category)
                    .type(CategoryType.valueOf(typeStr))
                    .date(sqlDate.toLocalDate())
                    .icon(emoji)
                    .build());

            charts.add(PieChart.builder()
                    .amount(amount)
                    .category(category)
                    .type(CategoryType.valueOf(typeStr))
                    .build());
        }
        return DashboardResponse.builder()
                .expense(chartItemDTORepon.getExpense())
                .income(chartItemDTORepon.getIncome())
                .balance(chartItemDTORepon.getIncome() - chartItemDTORepon.getExpense())
                .pieChart(charts)
                .recentTransactions(dtos)
                .build();
    }


}
