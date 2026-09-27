package com.verygroup.credit.repository;

import com.verygroup.credit.model.Transaction;
import java.util.List;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    List<Transaction> findByCustomerId(String customerId);
}
