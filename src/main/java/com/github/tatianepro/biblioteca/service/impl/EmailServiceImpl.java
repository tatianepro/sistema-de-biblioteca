package com.github.tatianepro.biblioteca.service.impl;

import com.github.tatianepro.biblioteca.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${application.mail.default-sender}")
    private String sender;

    @Override
    public void sendMails(String message, List<String> mailList) {

        String[] mailings = mailList.toArray(new String[mailList.size()]);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(sender);
        mailMessage.setSubject("Livro com empréstimo atrasado");
        mailMessage.setText(message);
        mailMessage.setTo(mailings);

        javaMailSender.send(mailMessage);
    }
}
