package com.bankapp.service;

import com.bankapp.entity.Transaction;
import com.bankapp.entity.User;
import com.bankapp.payload.EmailDetails;
import com.bankapp.repository.TransactionRepository;

import com.bankapp.repository.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class BankStatement {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    private static final String FILE = "C:\\Users\\abhim\\Downloads\\BankStatement.pdf";
    /**
     * Retrieve list of transactions within a date range given an account number
     * Generate a pdf file of transactions
     * send the pdf file via email
     */

    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws FileNotFoundException, DocumentException {
        LocalDateTime start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDateTime end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE);

        List<Transaction> transactionList = transactionRepository.findAll().stream()
                .filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> transaction.getCreatedAt().isEqual(start))
                .filter(transaction -> transaction.getCreatedAt().isEqual(end)).collect(Collectors.toList());

        User user = userRepository.findByAccountNumber(accountNumber);
        String customerName = user.getFirstName() + " " +user.getLastName() + " " +user.getLastName();
        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize);
        log.info("Setting size of document");
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable bankInfoTable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("The Real Bank"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.BLUE);
        bankName.setPadding(20f);

        PdfPCell bankAddress = new PdfPCell(new Phrase("The Bank Address"));
        bankAddress.setBorder(0);
        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);

        PdfPTable statementInfo = new PdfPTable(2);
        PdfPCell customerInfo = new PdfPCell(new Phrase("Start Date: "+startDate));
        customerInfo.setBorder(0);
        PdfPCell statement = new PdfPCell(new Phrase("Statement of Account"));
        statement.setBorder(0);
        PdfPCell lastDate = new PdfPCell(new Phrase("End Date: "+endDate));
        lastDate.setBorder(0);
        PdfPCell name = new PdfPCell(new Phrase("Customer Name: "+customerName));
        name.setBorder(0);
        PdfPCell space = new PdfPCell();
        PdfPCell address = new PdfPCell(new Phrase("Customer Address: "+user.getAddress()));
        address.setBorder(0);

        PdfPTable transactionTable = new PdfPTable(4);
        PdfPCell date = new PdfPCell(new Phrase("Date"));
        date.setBackgroundColor(BaseColor.BLUE);
        date.setBorder(0);
        PdfPCell transactionType = new PdfPCell(new Phrase("Transaction Type"));
        transactionType.setBackgroundColor(BaseColor.BLUE);
        transactionType.setBorder(0);
        PdfPCell transactionAmount = new PdfPCell(new Phrase("Transaction Amount"));
        transactionAmount.setBackgroundColor(BaseColor.BLUE);
        transactionAmount.setBorder(0);
        PdfPCell status = new PdfPCell(new Phrase("Status"));
        status.setBackgroundColor(BaseColor.BLUE);
        status.setBorder(0);

        transactionTable.addCell(date);
        transactionTable.addCell(transactionType);
        transactionTable.addCell(transactionAmount);
        transactionTable.addCell(status);

        transactionList.forEach(transaction -> {
            transactionTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionTable.addCell(new Phrase(transaction.getTransactionType()));
            transactionTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionTable.addCell(new Phrase(transaction.getStatus()));
        });

        statementInfo.addCell(customerInfo);
        statementInfo.addCell(statement);
        statementInfo.addCell(endDate);
        statementInfo.addCell(name);
        statementInfo.addCell(space);
        statementInfo.addCell(address);

        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(transactionTable);

        document.close();

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("Statement of the Account")
                .messageBody("Kindly find ur requested account statement attached!")
                .attachment(FILE)
                .build();
        return transactionList;

    }

    private void designStatement(List<Transaction> transactions) throws FileNotFoundException, DocumentException {


    }

}
