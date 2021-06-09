package com.github.tatianepro.biblioteca.service.impl;

import com.github.tatianepro.biblioteca.api.exception.BusinessException;
import com.github.tatianepro.biblioteca.model.entity.Loan;
import com.github.tatianepro.biblioteca.model.repository.LoanRepository;
import com.github.tatianepro.biblioteca.service.LoanService;
import org.springframework.stereotype.Service;

@Service
public class LoanServiceImpl implements LoanService {

    private LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public Loan save(Loan loan) {
        if (loanRepository.existsByBookAndNotReturned(loan.getBook())) {
            throw new BusinessException("Book already borrowed.");
        }
        return loanRepository.save(loan);
    }
}
