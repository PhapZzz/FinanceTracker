package com.financetracker.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.financetracker.api.FinanceTrackerApplication;
import com.financetracker.api.TestSecurityConfiguration;
import com.financetracker.api.WithMockCustomUser;
import com.financetracker.api.dto.DashboardResponse;
import com.financetracker.api.dto.PieChart;
import com.financetracker.api.dto.RecentTransactionsDTO;
import com.financetracker.api.enums.CategoryType;
import com.financetracker.api.service.DashboardService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// tắt filter, cho pphep1goi cac bean trung ten nhau trong cau hinh test, khong anh huong den app
@SpringBootTest(
        classes = FinanceTrackerApplication.class,
        properties = "spring.main.allow-bean-definition-overriding=true"
)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfiguration.class)
public class DashboardControllerTestGet {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String endpoint = "/api/dashboard";

    private DashboardResponse mockDashboardResponse() {

        return DashboardResponse.builder()
                .income(13000.0)
                .expense(3000.0)
                .balance(10000.0)
                .pieChart(List.of(
                        new PieChart("Education", 500.0, CategoryType.EXPENSE),
                        new PieChart("Freelance", 8000.0, CategoryType.INCOME),
                        new PieChart("Salary", 5000.0, CategoryType.INCOME)
                ))
                .recentTransactions(List.of(
                        new RecentTransactionsDTO(5L, "Education", "🎓", new BigDecimal("500.00"), LocalDate.of(2025, 1, 5), CategoryType.EXPENSE),
                        new RecentTransactionsDTO(4L, "Freelance", "💼", new BigDecimal("8000.00"), LocalDate.of(2025, 1, 5), CategoryType.INCOME),
                        new RecentTransactionsDTO(3L, "Salary", "💵", new BigDecimal("5000.00"), LocalDate.of(2025, 1, 3), CategoryType.INCOME)
                        //new RecentTransactionsDTO(3L, "Salary", "💵", new BigDecimal("5000.00"), "ss", CategoryType.INCOME)
                ))
                .build();
    }

// test get
    @Test
    @WithMockCustomUser(userId = 1, email = "user@gmail.com")
    void testGetDashboardData_fullCheck() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        given(dashboardService.getDashboard(anyInt(), anyInt()))
                .willReturn(mockDashboardResponse());

        MvcResult result =  mockMvc.perform(get(endpoint)
                        .param("month", "1")
                        .param("year", "2025")
                        .accept(MediaType.APPLICATION_JSON)

                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data.income").value(13000.0))
                .andExpect(jsonPath("$.data.expense").value(3000.0))
                .andExpect(jsonPath("$.data.balance").value(10000.0))
                .andExpect(jsonPath("$.data.pieChart[0].type").value("EXPENSE"))
                .andExpect(jsonPath("$.data.pieChart[1].category").value("Freelance"))
                .andExpect(jsonPath("$.data.recentTransactions[2].category").value("Salary"))
                .andReturn();
        // In ra response body
        System.out.println("Response body: " + result.getResponse().getContentAsString());
        System.out.println(">>> EMPTY? " + result.getResponse().getContentAsString().isEmpty());

    }


    // test controller/ sucess
    @Test
    @WithMockCustomUser(userId = 1, email = "user@gmail.com")
    void testJsonEndpoint_returnsSuccessResponse() throws Exception {
        mockMvc.perform(get("/api/dashboard/test")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data").value("test-data"));
    }

}
