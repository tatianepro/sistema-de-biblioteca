package com.github.tatianepro.biblioteca.service;

import com.github.tatianepro.biblioteca.model.entity.Books;
import com.github.tatianepro.biblioteca.model.entity.Loan;
import com.github.tatianepro.biblioteca.model.repository.LoanRepository;
import com.github.tatianepro.biblioteca.service.impl.LoanServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    LoanService loanService;

    @MockBean
    LoanRepository loanRepository;

    @BeforeEach
    public void setUp() {
        this.loanService = new LoanServiceImpl(loanRepository);
    }

    @Test
    @DisplayName("Deve salvar um empréstimo.")
    public void saveLoanTest() {
        //cenario
        String customerName = "Fulano";
        Long id = 1L;
        Books book = Books.builder().id(id).build();

        Loan savingLoan = Loan.builder().customer(customerName).book(book).loanDate(LocalDate.now()).build();
        Loan savedLoan = Loan.builder().id(id).customer(customerName).book(book).loanDate(LocalDate.now()).build();

        Mockito.when(loanRepository.save(savingLoan)).thenReturn(savedLoan);

        //execucao
        Loan loan = loanService.save(savingLoan);

        //verificacao
        Assertions.assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        Assertions.assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        Assertions.assertThat(loan.getBook()).isEqualTo(savedLoan.getBook());
        Assertions.assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());

    }
}
