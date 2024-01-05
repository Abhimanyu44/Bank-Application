package com.bankapp.service;

import com.bankapp.payload.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
    void sendEmailWithAttachment(EmailDetails details);
}
