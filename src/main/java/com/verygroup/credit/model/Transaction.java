package com.verygroup.credit.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record Transaction(UUID id, String customerId, TransactionType type,
                          BigDecimal amount, String description, LocalDate date) {}
