package com.verygroup.credit.api;

import java.math.BigDecimal;

public record CreditSummaryResponse(String customerId, BigDecimal creditLimit,
                                    BigDecimal currentBalance,
                                    BigDecimal creditUtilisationPercentage,
                                    BigDecimal totalPurchases, BigDecimal totalPayments,
                                    Integer estimatedMonthsToPayOff) {}
