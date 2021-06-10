package com.github.tatianepro.biblioteca.service;

import com.github.tatianepro.biblioteca.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface LoanService {
    Loan save(Loan loan);
    Optional<Loan> getById(Long id);
    Loan update(Loan loan);
    Page<Loan> find(Loan loan, Pageable pageRequest);
}
