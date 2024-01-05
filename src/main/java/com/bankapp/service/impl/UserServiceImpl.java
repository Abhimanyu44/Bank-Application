package com.bankapp.service.impl;

import com.bankapp.config.JwtTokenProvider;
import com.bankapp.entity.Role;
import com.bankapp.entity.User;
import com.bankapp.payload.*;
import com.bankapp.repository.UserRepository;
import com.bankapp.service.EmailService;
import com.bankapp.service.TransactionService;
import com.bankapp.service.UserService;
import com.bankapp.utils.AccountUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private EmailService emailService;
    private TransactionService transactionService;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider tokenProvider;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        /**
         * Create a new user account - saving the new user into db
         * Check if user already has an account
         */
        // Check if user already has an account
        if(userRepository.existByEmail(userRequest.getEmail())){

            BankResponse response = BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        // Create new user account
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountnumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .phoneNumber(userRequest.getPhoneNumber())
                .alternatePhoneNumber(userRequest.getAlternatePhoneNumber())
                .status("Active")
                .role(Role.valueOf("ROLE_ADMIN"))
                .build() ;

        User savedUser = userRepository.save(newUser);
        // Send Email Alert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT_CREATION")
                .messageBody("Congratulations! Your account has been created successfully."
                        + "\nYour Account Details are : "
                        + "\nAccount Name "+ savedUser.getFirstName()+ " " +savedUser.getLastName()+ " " +savedUser.getOtherName()
                        + "\nAccount Number : " + savedUser.getAccountNumber()
                        + "\nAccount Balance : "+ savedUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(emailDetails);
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(savedUser.getFirstName()+ " " +savedUser.getLastName()+ " " +savedUser.getOtherName())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountBalance(savedUser.getAccountBalance())
                        .build())
                .build();
    }

    public BankResponse login(LoginDto loginDto){
        Authentication authentication = null;
        authentication =authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );
        EmailDetails loginAlert = EmailDetails.builder()
                .subject("Yoy're logged in successfully!")
                .recipient(loginDto.getEmail())
                .messageBody("You're logged into your accountm If you did not initiate this request, please contact ur bank.")
                .build();
        emailService.sendEmailAlert(loginAlert);
        return BankResponse.builder()
                .responseCode("Login success")
                .responseMessage("Token: " +tokenProvider.generateToken(authentication))
                .build();
    }

    // Balance Enquiry, Name Enquiry, Credit, Debit, TransferRequest Money

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        // Check if the provided account number exists in the db
        boolean isAccountExists = userRepository.existByAccountNumber(request.getAccountNumber());
        if (!isAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();

        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                        .accountNumber(foundUser.getAccountNumber())
                        .accountBalance(foundUser.getAccountBalance())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        // Check if the provided account number exists in the db
        boolean isAccountExists = userRepository.existByAccountNumber(request.getAccountNumber());
        if(!isAccountExists){
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }

        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());

            return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        // Check if the provided account number exists in the db
        boolean isAccountExists = userRepository.existByAccountNumber(request.getAccountNumber());
        if(!isAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);

        // Save Transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();

        transactionService.saveTransactions(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getOtherName())
                        .accountNumber(userToCredit.getAccountNumber())
                        .accountBalance(userToCredit.getAccountBalance())
                        .build())

                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        // Check if the provided account number exists in the db
        boolean isAccountExists = userRepository.existByAccountNumber(request.getAccountNumber());
        if(!isAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();

        }
        // Check if the amount intended to withdraw is not more than the available account balance
        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();
        if(availableBalance.intValue() < debitAmount.intValue()){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userToDebit);
            // Save Transaction
            TransactionDto transactionDto = TransactionDto.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("DEBIT")
                    .amount(request.getAmount())
                    .build();

            transactionService.saveTransactions(transactionDto);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
                            .accountNumber(userToDebit.getAccountNumber())
                            .accountBalance(userToDebit.getAccountBalance())
                            .build())
                    .build();
        }
    }

    @Override
    public BankResponse transfer(TransferRequest request) {
        // Get the account to debit (checkif it exists)
        // Check if the amount i'm debiting is not more than actual current balance
        // debit the account
        // Get the account to credit
        // Credit the account

        boolean isDestinationAccountExist = userRepository.existByAccountNumber(request.getDestinationAccountNumber());

        if (!isDestinationAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        if (request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
//-------------------Source Account Transaction Details-----------------------------------------------------------------------------------------------------------------------
        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        String sourceUserName = sourceAccountUser.getFirstName()+" "+sourceAccountUser.getLastName()+" "+sourceAccountUser.getOtherName();
//        String sourceAccountNumber = sourceAccountUser.getAccountNumber();
//        BigDecimal sourceAccountBalance = sourceAccountUser.getAccountBalance();
        userRepository.save(sourceAccountUser);
        EmailDetails debitAlert = EmailDetails.builder()
                .recipient(sourceAccountUser.getEmail())
                .subject("Debit Alert")
                .messageBody("The sum of amount "+ request.getAmount() + " has been debited from " + sourceUserName +
                        " Your current balance is "+ sourceAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(debitAlert);

//----------------- Destination Account Transaction Details----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        User desinationAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        desinationAccountUser.setAccountBalance(desinationAccountUser.getAccountBalance().add(request.getAmount()));
     //   String recipientUserName = desinationAccountUser.getFirstName()+" "+desinationAccountUser.getLastName()+" "+desinationAccountUser.getOtherName();
        userRepository.save(desinationAccountUser);
        EmailDetails creditAlert = EmailDetails.builder()
                .recipient(sourceAccountUser.getEmail())
                .subject("Credit Alert")
                .messageBody("The sum of amount "+ request.getAmount() +" has been credited to your account from " +sourceUserName+
                        "Your current balance is "+ sourceAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(creditAlert);

        // Save Transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(desinationAccountUser.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();

        transactionService.saveTransactions(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFULL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFULL_MESSAGE)
                .accountInfo(null)
                .build();
    }

}