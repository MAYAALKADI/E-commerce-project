package com.example.store_project.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // الإرسال الفعلي. أي خطأ هون بيطلع للـ Worker فيعمل Retry
    public void sendOrderConfirmation(String to, Long orderId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("store@example.com");
        message.setTo(to);
        message.setSubject("Order confirmation #" + orderId);
        message.setText("Thank you! Your order #" + orderId + " has been received.");
        mailSender.send(message);
        log.info("Email sent to {} for order {}", to, orderId);
    }
}