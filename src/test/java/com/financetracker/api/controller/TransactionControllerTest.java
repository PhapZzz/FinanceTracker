package com.financetracker.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financetracker.api.FinanceTrackerApplication;
import com.financetracker.api.TestSecurityConfiguration;
import com.financetracker.api.WithMockCustomUser;
import com.financetracker.api.enums.CategoryType;
import com.financetracker.api.mapper.TransactionMapper;
import com.financetracker.api.request.TransactionRequest;
import com.financetracker.api.response.TransactionHistoryResponse;
import com.financetracker.api.response.TransactionResponse;
import com.financetracker.api.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
@SpringBootTest(
        classes = FinanceTrackerApplication.class,
        properties = "spring.main.allow-bean-definition-overriding=true"
)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfiguration.class)// Bỏ qua cấu hình security thật
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;
    private TransactionMapper transactionMapper;

    @Autowired
    private ObjectMapper objectMapper;

// test trường hợp đúng
    @Test
    @WithMockCustomUser(userId = 1, email = "user@gmail.com")
    void testCreateTransaction_Success() throws Exception {
        // Arrange
        TransactionRequest request = new TransactionRequest();
        request.setAmount(BigDecimal.valueOf(150.00));
        request.setCategoryId(1L);
        request.setNote("Lunch");
        request.setDate("2025-07-27");


// Parse ngày
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(request.getDate());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd");
        }

        TransactionResponse response = TransactionResponse.builder()
                .id(1L)
                .amount(request.getAmount())
                .note(request.getNote())
                .date(parsedDate)
                .category(TransactionResponse.CategoryDTO.builder()
                        .id(1L)
                        .name("Housing")
                        .type("EXPENSE")
                        .emoji("\uD83C\uDFE0")
                        .iconUrl("https://cdn.example.com/icons/housing.png")
                        .build())
                .build();

        given(transactionService.addTransaction(any(TransactionRequest.class),eq(1L))).willReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.amount", is(150.00)))
                .andExpect(jsonPath("$.data.note", is("Lunch")))
                .andExpect(jsonPath("$.data.category.id", is(1)))
                .andExpect(jsonPath("$.data.category.name", is("Housing")))
                .andExpect(jsonPath("$.data.category.type", is("EXPENSE")))
                .andExpect(jsonPath("$.data.category.emoji", is("\uD83C\uDFE0")))
                .andExpect(jsonPath("$.data.category.iconUrl", is("https://cdn.example.com/icons/housing.png")));

    }



    @Test
    @WithMockCustomUser(userId = 1, email = "user@gmail.com")
    void testGetTransactionHistory_Success() throws Exception {
        // Mock dữ liệu
        TransactionHistoryResponse historyResponse = TransactionHistoryResponse.builder()
                .id(1L)
                .amount(new BigDecimal("50000"))
                .date(LocalDate.of(2025, 7, 27))
                .category(TransactionHistoryResponse.CategoryDTO.builder()
                        .name("Housing")
                        .type("EXPENSE")
                        .emoji("\uD83C\uDFE0")
                        .iconUrl("https://cdn.example.com/icons/housing.png")
                        .build())
                .build();

        List<TransactionHistoryResponse> content = List.of(historyResponse);

        Page<TransactionHistoryResponse> mockPage = new PageImpl<>(
                content,
                PageRequest.of(0, 10),
                1
        );

        Mockito.when(transactionService.getTransactionHistory(
                anyLong(),
                any(), any(), any(), any(), anyInt(), anyInt()
        )).thenReturn(mockPage);

        mockMvc.perform(get("/api/transactions/history")
                        .param("page", "1")
                        .param("size", "10")
                        .param("startDate", "2025-07-01")
                        .param("endDate", "2025-07-30")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Transaction history fetched successfully"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].amount").value("50000"))
                .andExpect(jsonPath("$.data[0].date[0]").value(2025))    // trong response không @JsonFormat(pattern = "yyyy-MM-dd")
                .andExpect(jsonPath("$.data[0].date[1]").value(7))       // nên khi localDate chuyển sang JSON sẽ thành mảng
                .andExpect(jsonPath("$.data[0].date[2]").value(27))
                .andExpect(jsonPath("$.pagination.currentPage").value(1))
                .andExpect(jsonPath("$.pagination.totalItems").value(1));
    }


}



