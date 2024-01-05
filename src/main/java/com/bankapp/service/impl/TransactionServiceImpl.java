package com.bankapp.service.impl;

import com.bankapp.entity.Transaction;
import com.bankapp.payload.TransactionDto;
import com.bankapp.repository.TransactionRepository;
import com.bankapp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component  // @Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Override
    public void saveTransactions(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .accountNumber(transactionDto.getAccountNumber())
                .amount(transactionDto.getAmount())
                .status("Success")
                .build();
        transactionRepository.save(transaction);
        System.out.println("Transaction saved successfully");

    }
}
