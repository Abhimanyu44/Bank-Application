package com.bankapp.controller;

import com.bankapp.entity.Transaction;
import com.bankapp.service.BankStatement;
import com.itextpdf.text.DocumentException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/bankstatement")
@AllArgsConstructor
public class TransactionController {

    @Autowired
    private BankStatement bankStatement;

    @PostMapping
    public List<Transaction> generateBankStatement(@RequestParam String accountNumber,
                                                   @RequestParam String startDate,
                                                   @RequestParam String endDate) throws DocumentException, FileNotFoundException {

        return bankStatement.generateStatement(accountNumber,startDate,endDate);
    }


}
