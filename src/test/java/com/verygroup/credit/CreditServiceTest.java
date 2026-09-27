package com.verygroup.credit;

import com.verygroup.credit.api.CreateTransactionRequest;
import com.verygroup.credit.api.CreditSummaryResponse;
import com.verygroup.credit.model.TransactionType;
import com.verygroup.credit.repository.InMemoryTransactionRepository;
import com.verygroup.credit.service.CreditService;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CreditServiceTest {
    private CreditService service;

    @BeforeEach
    void setUp() { service = new CreditService(new InMemoryTransactionRepository()); }

    @Test
    void shouldCalculateBalanceAndUtilisation() {
        service.addTransaction(tx(TransactionType.PURCHASE, "120.00", "Headphones", "2026-09-01"));
        service.addTransaction(tx(TransactionType.PURCHASE, "80.00", "Keyboard", "2026-09-02"));
        service.addTransaction(tx(TransactionType.PAYMENT, "50.00", "Monthly payment", "2026-09-15"));

        CreditSummaryResponse summary = service.getCreditSummary("customer-1");

        assertThat(summary.currentBalance()).isEqualByComparingTo("150.00");
        assertThat(summary.creditUtilisationPercentage()).isEqualByComparingTo("7.50");
        assertThat(summary.totalPurchases()).isEqualByComparingTo("200.00");
        assertThat(summary.totalPayments()).isEqualByComparingTo("50.00");
        assertThat(summary.estimatedMonthsToPayOff()).isEqualTo(3);
    }

    @Test
    void shouldReturnZeroBalanceAfterPaymentsCoverPurchases() {
        service.addTransaction(tx(TransactionType.PURCHASE, "100.00", "Purchase", "2026-09-01"));
        service.addTransaction(tx(TransactionType.PAYMENT, "100.00", "Payment", "2026-09-05"));

        CreditSummaryResponse summary = service.getCreditSummary("customer-1");

        assertThat(summary.currentBalance()).isEqualByComparingTo("0.00");
        assertThat(summary.creditUtilisationPercentage()).isEqualByComparingTo("0.00");
        assertThat(summary.estimatedMonthsToPayOff()).isZero();
    }

    @Test
    void shouldKeepCustomersIsolated() {
        service.addTransaction(tx("customer-1", TransactionType.PURCHASE, "100.00", "Purchase", "2026-09-01"));
        service.addTransaction(tx("customer-2", TransactionType.PURCHASE, "900.00", "Purchase", "2026-09-01"));

        assertThat(service.getTransactions("customer-1")).hasSize(1);
        assertThat(service.getCreditSummary("customer-1").currentBalance()).isEqualByComparingTo("100.00");
    }

    private CreateTransactionRequest tx(TransactionType type, String amount, String description, String date) {
        return tx("customer-1", type, amount, description, date);
    }

    private CreateTransactionRequest tx(String customerId, TransactionType type, String amount, String description, String date) {
        return new CreateTransactionRequest(customerId, type, new BigDecimal(amount), description, LocalDate.parse(date));
    }
}
