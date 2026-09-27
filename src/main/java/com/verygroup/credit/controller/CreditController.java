package com.verygroup.credit.controller;

import com.verygroup.credit.api.CreateTransactionRequest;
import com.verygroup.credit.api.CreditSummaryResponse;
import com.verygroup.credit.api.TransactionResponse;
import com.verygroup.credit.service.CreditService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@Validated
public class CreditController {
    private final CreditService creditService;

    public CreditController(CreditService creditService) {
        this.creditService = creditService;
    }

    @PostMapping("/transactions")
    public TransactionResponse addTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        return creditService.addTransaction(request);
    }

    @GetMapping("/transactions")
    public List<TransactionResponse> getTransactions(
            @RequestParam @NotBlank(message = "customerId is required") String customerId) {
        return creditService.getTransactions(customerId);
    }

    @GetMapping("/credit-summary")
    public CreditSummaryResponse getCreditSummary(
            @RequestParam @NotBlank(message = "customerId is required") String customerId) {
        return creditService.getCreditSummary(customerId);
    }
}
