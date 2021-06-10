package com.github.tatianepro.biblioteca.service;

import com.github.tatianepro.biblioteca.api.exception.BusinessException;
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
import java.util.Optional;

import static org.assertj.core.api.ThrowableAssert.catchThrowable;

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

        Mockito.when(loanRepository.existsByBookAndNotReturned(savingLoan.getBook())).thenReturn(false);
        Mockito.when(loanRepository.save(savingLoan)).thenReturn(savedLoan);

        //execucao
        Loan loan = loanService.save(savingLoan);

        //verificacao
        Assertions.assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        Assertions.assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        Assertions.assertThat(loan.getBook()).isEqualTo(savedLoan.getBook());
        Assertions.assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());

    }

    @Test
    @DisplayName("Deve lançar um erro de negócio ao salvar o empréstimo de um livro que já está emprestado.")
    public void loanedBookErrorOnSaveLoanTest() {
        //cenario
        Loan savingLoan = createLoan();

        Mockito.when(loanRepository.existsByBookAndNotReturned(savingLoan.getBook())).thenReturn(true);

        //execucao
        Throwable exception = catchThrowable(() -> loanService.save(savingLoan));

        //verificacao
        Assertions.assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already borrowed.");

        Mockito.verify(loanRepository, Mockito.never()).save(savingLoan);

    }

    @Test
    @DisplayName("Deve obter as informações de um empréstimo pelo ID")
    public void getLoanDetailsTest() {
        //cenario
        Long id = 1L;
        Loan loan = createLoan();
        loan.setId(id);

        Mockito.when(loanRepository.findById(id)).thenReturn(Optional.of(loan));

        //execucao
        Optional<Loan> loanFound = loanService.getById(id);

        //verificacao
        Assertions.assertThat(loanFound.isPresent()).isTrue();
        Assertions.assertThat(loanFound.get().getId()).isEqualTo(loan.getId());
        Assertions.assertThat(loanFound.get().getCustomer()).isEqualTo(loan.getCustomer());
        Assertions.assertThat(loanFound.get().getBook()).isEqualTo(loan.getBook());
        Assertions.assertThat(loanFound.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        Mockito.verify(loanRepository).findById(id);
    }

    private Loan createLoan() {
        Books book = Books.builder().id(1L).build();
        String customerName = "Fulano";

        Loan savingLoan = Loan.builder().customer(customerName).book(book).loanDate(LocalDate.now()).build();
        return savingLoan;
    }

}
