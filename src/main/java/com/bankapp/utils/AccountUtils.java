package com.bankapp.utils;

import java.time.Year;

public class AccountUtils {

    public static  final  String ACCOUNT_EXIST_CODE = "001";
    public static final String ACCOUNT_EXIST_MESSAGE = "This user already has an account created!";
    public static final String ACCOUNT_CREATION_SUCCESS_CODE = "002";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account has been created successfully";
    public static  final  String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "User with the provide account not exists!";
    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_MESSAGE = "Account has been found";
    public static final String ACCOUNT_CREDITED_SUCCESS_CODE = "005";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "User Account has been credited successfully ";
    public static final String ACCOUNT_DEBITED_SUCCESS_CODE = "006";
    public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE = "User Account has been debited successfully ";
    public static final String INSUFFICIENT_BALANCE_CODE = "007";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient Balance";

    public static  final  String TRANSFER_SUCCESSFULL_CODE = "008";
    public static final String TRANSFER_SUCCESSFULL_MESSAGE = "User with the provide account not exists!";
//    public static  final  String TRANSFER_SUCCESSFULL_CODE = "008";
//    public static final String TRANSFER_SUCCESSFULL_MESSAGE = "User with the provide account not exists!";


    public static String generateAccountnumber() {


        /**
         * 2023 + randomSixDigits----(2023112233)
         */
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;

        // generate random number btw min and max

        int randnumber = (int) Math.floor(Math.random() * (max - min + 1) + min);

        // convert the currentYear and randomNumber to strings, then concatenate them

        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randnumber);
        StringBuilder accountNumber = new StringBuilder();

        return accountNumber.append(year).append(randomNumber).toString();

    }

}
