package com.github.tatianepro.biblioteca.service;

import com.github.tatianepro.biblioteca.model.entity.Loan;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchedulingService {

    private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";

    @Value("${application.mail.lateLoans.message}")
    private String message;

    private final LoanService loanService;
    private final EmailService emailService;

    @Scheduled(cron = CRON_LATE_LOANS)
    public void sendEmailToLateLoans() {
        List<Loan> allLateLoans = loanService.getAllLateLoans();
        List<String> mailList = allLateLoans
                .stream()
                .map(loan -> loan.getCustomerEmail())
                .collect(Collectors.toList());
        emailService.sendMails(message, mailList);
    }

}
