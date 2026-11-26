package com.naveen.employeemanagement.activation.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class ActivationEmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public ActivationEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendActivationEmail(
            String email,
            String fullName,
            String activationToken) {

        String activationLink =
                frontendUrl + "/activate?token=" + activationToken;

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(email);
            helper.setSubject(
                    "Activate Your TATTVAM PEOPLE Account");

            String emailBody = """
                    <html>
                    <body>
                        <h2>Welcome to TATTVAM PEOPLE!</h2>

                        <p>Hello %s,</p>

                        <p>Your registration was successful.</p>

                        <p>To activate your account and create your password,
                        please click the link below:</p>

                        <p>
                            <a href="%s">Activate My Account</a>
                        </p>

                        <p>This activation link is valid for 24 hours.</p>

                        <p>If you did not request this account,
                        please ignore this email.</p>

                        <br>

                        <p>
                            Regards,<br>
                            TATTVAM PEOPLE Team
                        </p>
                    </body>
                    </html>
                    """.formatted(fullName, activationLink);

            helper.setText(emailBody, true);

            mailSender.send(message);

        } catch (MessagingException exception) {
            throw new IllegalStateException(
                    "Unable to send activation email", exception);
        }
    }
}