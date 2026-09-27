package com.verygroup.credit.api;

import com.verygroup.credit.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionRequest(
        @NotBlank(message = "customerId is required") String customerId,
        @NotNull(message = "type is required") TransactionType type,
        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "amount must be greater than zero") BigDecimal amount,
        @NotBlank(message = "description is required") String description,
        LocalDate date) {}
