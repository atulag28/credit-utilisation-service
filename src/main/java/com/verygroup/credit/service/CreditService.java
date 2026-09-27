package com.verygroup.credit.service;

import com.verygroup.credit.api.CreateTransactionRequest;
import com.verygroup.credit.api.CreditSummaryResponse;
import com.verygroup.credit.api.TransactionResponse;
import com.verygroup.credit.model.Transaction;
import com.verygroup.credit.model.TransactionType;
import com.verygroup.credit.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class CreditService {
    private static final BigDecimal CREDIT_LIMIT = new BigDecimal("2000.00");
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final TransactionRepository transactionRepository;

    public CreditService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionResponse addTransaction(CreateTransactionRequest request) {
        Transaction transaction = new Transaction(
                UUID.randomUUID(), request.customerId().trim(), request.type(),
                request.amount().setScale(2, RoundingMode.HALF_UP), request.description().trim(),
                request.date() == null ? LocalDate.now() : request.date());
        return toResponse(transactionRepository.save(transaction));
    }

    public List<TransactionResponse> getTransactions(String customerId) {
        return transactionRepository.findByCustomerId(customerId.trim()).stream()
                .map(this::toResponse).toList();
    }

    public CreditSummaryResponse getCreditSummary(String customerId) {
        String id = customerId.trim();
        List<Transaction> transactions = transactionRepository.findByCustomerId(id);
        BigDecimal purchases = totalOf(transactions, TransactionType.PURCHASE);
        BigDecimal payments = totalOf(transactions, TransactionType.PAYMENT);
        BigDecimal balance = purchases.subtract(payments).max(BigDecimal.ZERO);
        BigDecimal utilisation = balance.divide(CREDIT_LIMIT, 6, RoundingMode.HALF_UP)
                .multiply(HUNDRED).setScale(2, RoundingMode.HALF_UP);

        return new CreditSummaryResponse(id, CREDIT_LIMIT, balance.setScale(2, RoundingMode.HALF_UP),
                utilisation, purchases.setScale(2, RoundingMode.HALF_UP),
                payments.setScale(2, RoundingMode.HALF_UP), estimatePayoffMonths(transactions, balance));
    }

    private BigDecimal totalOf(List<Transaction> transactions, TransactionType type) {
        return transactions.stream().filter(t -> t.type() == type).map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Integer estimatePayoffMonths(List<Transaction> transactions, BigDecimal balance) {
        if (balance.signum() == 0) return 0;

        List<Transaction> payments = transactions.stream()
                .filter(t -> t.type() == TransactionType.PAYMENT).toList();
        if (payments.isEmpty()) return null;

        LocalDate first = payments.stream().map(Transaction::date).min(LocalDate::compareTo).orElseThrow();
        LocalDate last = payments.stream().map(Transaction::date).max(LocalDate::compareTo).orElseThrow();
        long months = ChronoUnit.MONTHS.between(first.withDayOfMonth(1), last.withDayOfMonth(1)) + 1;
        BigDecimal averageMonthlyPayment = totalOf(payments, TransactionType.PAYMENT)
                .divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);

        if (averageMonthlyPayment.signum() == 0) return null;
        return balance.divide(averageMonthlyPayment, 0, RoundingMode.CEILING).intValue();
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(transaction.id(), transaction.customerId(), transaction.type(),
                transaction.amount(), transaction.description(), transaction.date());
    }
}
