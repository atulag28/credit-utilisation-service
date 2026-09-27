package com.verygroup.credit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.verygroup.credit.api.CreateTransactionRequest;
import com.verygroup.credit.controller.CreditController;
import com.verygroup.credit.model.TransactionType;
import com.verygroup.credit.repository.InMemoryTransactionRepository;
import com.verygroup.credit.service.CreditService;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CreditControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        CreditService service = new CreditService(new InMemoryTransactionRepository());
        mockMvc = MockMvcBuilders.standaloneSetup(new CreditController(service)).build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void shouldCreateTransaction() throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest("customer-1", TransactionType.PURCHASE,
                new BigDecimal("120.00"), "Sony Headphones", LocalDate.of(2026, 9, 20));

        mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("customer-1"))
                .andExpect(jsonPath("$.type").value("PURCHASE"))
                .andExpect(jsonPath("$.amount").value(120.00));
    }

    @Test
    void shouldReturnCreditSummary() throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest("customer-1", TransactionType.PURCHASE,
                new BigDecimal("500.00"), "Laptop", LocalDate.of(2026, 9, 20));

        mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/credit-summary").param("customerId", "customer-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creditLimit").value(2000.00))
                .andExpect(jsonPath("$.currentBalance").value(500.00))
                .andExpect(jsonPath("$.creditUtilisationPercentage").value(25.00));
    }
}
