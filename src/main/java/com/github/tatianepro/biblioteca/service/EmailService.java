package com.github.tatianepro.biblioteca.service;

import java.util.List;

public interface EmailService {

    void sendMails(String message, List<String> mailList);
}
