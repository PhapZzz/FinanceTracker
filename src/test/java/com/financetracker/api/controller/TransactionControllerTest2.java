package com.financetracker.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financetracker.api.enums.RoleName;
import com.financetracker.api.request.TransactionRequest;
import com.financetracker.api.response.TransactionHistoryResponse;
import com.financetracker.api.response.TransactionResponse;
import com.financetracker.api.service.CustomUserDetails;
import com.financetracker.api.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)// ✅ Bỏ security filter
public class TransactionControllerTest2 {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private TransactionService transactionService;

    private CustomUserDetails mockUserDetails;

    @BeforeEach
    void setup() {
        mockUserDetails = new CustomUserDetails(
                com.financetracker.api.entity.User.builder()
                        .id(1L)
                        .email("user@example.com")
                        .password("password")
                        .role(com.financetracker.api.entity.Role.builder()
                                .name(RoleName.USER).build())
                        .build()
        );
    }

    @Test
    @DisplayName("POST /api/transactions - success")
    void testAddTransaction() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(BigDecimal.valueOf(100));
        request.setNote("Lunch");
        request.setCategoryId(2L);
        request.setDate("2025-07-27");

        TransactionResponse.CategoryDTO categoryDTO = TransactionResponse.CategoryDTO.builder()
                .id(2L)
                .name("Food")
                .type("EXPENSE")
                .emoji("🍔")
                .iconUrl("url")
                .build();

        TransactionResponse response = TransactionResponse.builder()
                .id(10L)
                .amount(BigDecimal.valueOf(100))
                .date(LocalDate.parse("2025-07-27"))
                .note("Lunch")
                .category(categoryDTO)
                .build();

        when(transactionService.addTransaction(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(authentication(new TestingAuthenticationToken(mockUserDetails, null))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(10L))
                .andExpect(jsonPath("$.data.amount").value(100))
                .andExpect(jsonPath("$.data.note").value("Lunch"))
                .andExpect(jsonPath("$.data.category.id").value(2L));
    }

    @Test
    @DisplayName("GET /api/transactions/history - success")
    void testGetTransactionHistory() throws Exception {
        TransactionHistoryResponse historyResponse = TransactionHistoryResponse.builder()
                .id(10L)
                .note("Coffee")
                .amount(BigDecimal.valueOf(50))
                .date(LocalDate.parse("2025-07-26"))
                .category(TransactionHistoryResponse.CategoryDTO.builder()
                        .name("Food")
                        .emoji("🍔")
                        .iconUrl("url")
                        .type("EXPENSE")
                        .build())
                .build();

        when(transactionService.getTransactionHistory(
                any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(historyResponse), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/transactions/history")
                        .with(authentication(new TestingAuthenticationToken(mockUserDetails, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(10L))
                .andExpect(jsonPath("$.data.content[0].note").value("Coffee"))
                .andExpect(jsonPath("$.data.content[0].amount").value(50));
    }
}
