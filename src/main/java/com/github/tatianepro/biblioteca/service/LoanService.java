package com.github.tatianepro.biblioteca.service;

import com.github.tatianepro.biblioteca.model.entity.Loan;
import org.springframework.stereotype.Service;

@Service
public interface LoanService {

    Loan save(Loan loan);
}
