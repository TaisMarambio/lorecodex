package com.lorecodex.backend.service;

public interface EmailService {
    void sendNotificationEmail(String to, String subject, String content);
}
