package com.consoleadmin.zer0balance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Slf4j
@RequiredArgsConstructor
public class ErrorReportingService {

    @Value("${admin.email}")
    private String adminEmail;

    @Autowired
    private EmailService emailService;

    public void reportError(String context, Exception e) {
        log.error("Error in {}: {}", context, e.getMessage(), e);

        String body = "<b>Error Report from Zer0Balance Job</b><br><br>"
                + "<strong>Context:</strong> " + context + "<br>"
                + "<strong>Message:</strong> " + e.getMessage() + "<br><br>"
                + "<pre style='background:#f2f2f2;padding:10px;border:1px solid #ddd;font-family:monospace;'>"
                + Arrays.toString(e.getStackTrace())
                + "</pre>";

        try {
            emailService.sendEmail(adminEmail, "Zer0Balance Job Failure - " + context, body);
        } catch (Exception ex) {
            log.error("Failed to send error report email: {}", ex.getMessage(), ex);
        }
    }
}
