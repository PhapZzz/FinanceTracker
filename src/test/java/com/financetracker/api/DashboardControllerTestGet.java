package com.financetracker.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financetracker.api.dto.DashboardResponse;
import com.financetracker.api.dto.PieChart;
import com.financetracker.api.dto.RecentTransactionsDTO;
import com.financetracker.api.entity.User;
import com.financetracker.api.enums.CategoryType;
import com.financetracker.api.repository.SummaryRepository;
import com.financetracker.api.response.SuccessResponse;
import com.financetracker.api.security.CustomUserDetails;
import com.financetracker.api.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest()
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ComponentScan("com.financetracker.api.controller")

public class DashboardControllerTestGet {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    private final String endpoint = "/api/dashboard";
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUpSecurityContext() {
        // Tạo User giả
        User user = new User();
        user.setId(1L);
//        user.setFullName("testuser");

        // Gói user vào CustomUserDetails
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // Mock Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Mock SecurityContext
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Gán vào SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetDashboardData() throws Exception {
        // Mock data
        List<PieChart> pieCharts = List.of(
                new PieChart("Education", 500.0, CategoryType.EXPENSE),
                new PieChart("Freelance", 8000.0, CategoryType.INCOME),
                new PieChart("Salary", 5000.0, CategoryType.INCOME)


        );

        List<RecentTransactionsDTO> recentTransactions = List.of(
                new RecentTransactionsDTO(5L, "Education", "🎓", new BigDecimal("500.00"), LocalDate.of(2025, 1, 5), CategoryType.EXPENSE),
                new RecentTransactionsDTO(4L, "Freelance", "💼", new BigDecimal("8000.00"), LocalDate.of(2025, 1, 5), CategoryType.INCOME),
                new RecentTransactionsDTO(3L, "Salary", "💵", new BigDecimal("5000.00"), LocalDate.of(2025, 1, 3), CategoryType.INCOME)
        );

        DashboardResponse dashboardResponse = DashboardResponse.builder()
                .income(13000.0)
                .expense(3000.0)
                .balance(13000.0 - 3000.0)
                .pieChart(pieCharts)
                .recentTransactions(recentTransactions)
                .build();

        // Mock service
        given(dashboardService.getDashboard(anyInt(), anyInt())).willReturn(dashboardResponse);

        // Perform GET request
        ResultActions response = mockMvc.perform(get(endpoint)
                .accept(MediaType.APPLICATION_JSON)
                .param("month", "1")
                .param("year", "2025"));


        response.andExpect(status().isOk());
        response.andDo(print());
//        response.andExpect(jsonPath("$.success").value("true"));
//        response.andExpect(jsonPath("$.message").value("ok"));
//        response.andExpect(jsonPath("$.data.income").value(13000.0));
//        response.andExpect(jsonPath("$.data.expense").value(3000.0));
//        response.andExpect(jsonPath("$.data.balance").value(10000.0));
//        response.andExpect(jsonPath("$.data.pieChart[0].type").value("EXPENSE"));
//        response.andExpect(jsonPath("$.data.pieChart[1].category").value("Freelance"));
//        response.andExpect(jsonPath("$.data.recentTransactions[2].category").value("Salary"));

    }


    @Test
    void testControllerMapping() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/dashboard")
                        .param("month", "1")
                        .param("year", "2025")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // Lấy nội dung body
        String content = result.getResponse().getContentAsString();
        System.out.println(">>> Body JSON: " + content);
    }


    @Test
    void testSuccessResponseOf() throws Exception {
        SuccessResponse<String> res = SuccessResponse.of("abc", "ok");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(res);
        System.out.println(json);
    }

    @Test
    void testDashboardSimple() throws Exception {
        DashboardResponse dashboardResponse = DashboardResponse.builder()
                .income(13000.0)
                .expense(3000.0)
                .balance(10000.0)
                .build();

        given(dashboardService.getDashboard(Mockito.anyInt(), Mockito.anyInt()))
                .willReturn(dashboardResponse);
        String json = objectMapper.writeValueAsString(SuccessResponse.of(null, "ok"));
        System.out.println(">>> JSON expected: " + json);

        mockMvc.perform(get("/api/dashboard")
                        .param("month", "1")
                        .param("year", "2025")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())

                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data.income").value(13000.0));

    }
}
