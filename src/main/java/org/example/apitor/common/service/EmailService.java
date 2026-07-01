package org.example.apitor.common.service;


import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);

        mailSender.send(message);
    }

    public void sendPasswordResetToken(String username,String email, String token) throws MessagingException{
        String htmlContent =  "<div style='font-family: \"Segoe UI\", Tahoma, Geneva, Verdana, sans-serif; max-width: 400px; margin: 0 auto; padding: 20px; border: 1px solid #eeeeee; border-radius: 12px; text-align: center;'>" +
                "<h1> Dear, " + username + "</h1>"+
                "<h2 style='color: #2c3e50; margin-bottom: 10px;'>Verification Code</h2>" +
                "<p style='color: #7f8c8d; font-size: 15px;'>Use the following OTP to reset your password. This code is valid for 5 minutes only.</p>" +


                "<div style='margin: 30px 0;'>" +
                "<span style='background-color: #f8f9fa; color: #2563eb; font-size: 32px; font-weight: bold; letter-spacing: 8px; padding: 15px 25px; border: 1px dashed #2563eb; border-radius: 8px; display: inline-block;'>" +
                "<b>" +token + "</b" +
                "</span>" +
                "</div>" +

                "<p style='color: #e74c3c; font-size: 13px;'><strong>Security Warning:</strong> Do not share this code with anyone.</p>" +
                "<hr style='border: 0; border-top: 1px solid #f0f0f0; margin: 20px 0;'>" +
                "<p style='color: #bdc3c7; font-size: 11px;'>If you didn't request this, please ignore this email.</p>" +
                "</div>";

        sendEmail(email, "Reset your APITOR Account Password", htmlContent);
    }
}

