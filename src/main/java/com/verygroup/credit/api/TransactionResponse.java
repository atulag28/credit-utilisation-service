package com.verygroup.credit.api;

import com.verygroup.credit.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponse(UUID id, String customerId, TransactionType type,
                                  BigDecimal amount, String description, LocalDate date) {}
