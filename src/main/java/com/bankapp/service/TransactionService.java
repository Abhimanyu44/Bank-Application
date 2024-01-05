package com.bankapp.service;

import com.bankapp.payload.TransactionDto;

public interface TransactionService {

    void saveTransactions(TransactionDto transactionDto);
}
