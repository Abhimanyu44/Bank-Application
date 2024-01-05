package com.bankapp.service;

import com.bankapp.payload.*;


public interface UserService {

    BankResponse createAccount(UserRequest userRequest);

    BankResponse login(LoginDto loginDto);

    BankResponse balanceEnquiry(EnquiryRequest request);

    String nameEnquiry(EnquiryRequest request);

    BankResponse creditAccount(CreditDebitRequest request);

    BankResponse debitAccount(CreditDebitRequest request);

    BankResponse transfer(TransferRequest request);


}
