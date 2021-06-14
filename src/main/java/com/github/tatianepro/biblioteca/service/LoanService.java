package com.github.tatianepro.biblioteca.service;

import com.github.tatianepro.biblioteca.model.entity.Books;
import com.github.tatianepro.biblioteca.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);
    Optional<Loan> getById(Long id);
    Loan update(Loan loan);
    Page<Loan> find(Loan loan, Pageable pageRequest);
    Page<Loan> getLoansByBook(Books book, Pageable pageRequest);
    List<Loan> getAllLateLoans();
}
