package com.github.tatianepro.biblioteca.service;

import com.github.tatianepro.biblioteca.api.resource.BookController;
import com.github.tatianepro.biblioteca.model.entity.Loan;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface LoanService {
    Loan save(Loan loan);
    Optional<Loan> getById(Long id);
    Loan update(Loan loan);
}
