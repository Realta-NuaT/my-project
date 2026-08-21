package org.example.service;

import org.example.entity.dto.EmailRecord;

import java.util.List;

public interface EmailService {
    void sendVerifyEmail(String type, String email, int code);
    List<EmailRecord> listEmailRecord();
}
