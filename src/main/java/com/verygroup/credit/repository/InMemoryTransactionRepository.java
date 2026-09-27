package com.verygroup.credit.repository;

import com.verygroup.credit.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class InMemoryTransactionRepository implements TransactionRepository {
    private final ConcurrentMap<String, List<Transaction>> transactionsByCustomer = new ConcurrentHashMap<>();

    @Override
    public Transaction save(Transaction transaction) {
        transactionsByCustomer.computeIfAbsent(transaction.customerId(), ignored -> new CopyOnWriteArrayList<>())
                .add(transaction);
        return transaction;
    }

    @Override
    public List<Transaction> findByCustomerId(String customerId) {
        return transactionsByCustomer.getOrDefault(customerId, List.of()).stream()
                .sorted(Comparator.comparing(Transaction::date).thenComparing(Transaction::id))
                .toList();
    }
}
