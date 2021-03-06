package com.github.tatianepro.biblioteca.model.repository;

import com.github.tatianepro.biblioteca.model.entity.Books;
import com.github.tatianepro.biblioteca.model.entity.Loan;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private TestEntityManager entityManagerTest;

    @Test
    @DisplayName("Deve verificar se existe empréstimo não devolvido para o livro")
    public void existsByBookAndNotReturnedTest() {
        //cenario
        Loan loan = createandPersistLoan(LocalDate.now());
        Books book = loan.getBook();

        //execucao
        boolean exists = loanRepository.existsByBookAndNotReturned(book);

        //verificacao
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve buscar empréstimo pelo isbn do livro ou customer")
    public void findByBookIsbnOrCustomerTest() {
        //cenario
        Loan loan = createandPersistLoan(LocalDate.now());

        //execucao
        Page<Loan> pageLoan = loanRepository.findByBookIsbnOrCustomer(
                "9781234567897", "Fulano", PageRequest.of(0, 10));

        //verificacao
        Assertions.assertThat(pageLoan.getContent()).hasSize(1);
        Assertions.assertThat(pageLoan.getContent()).contains(loan);
        Assertions.assertThat(pageLoan.getPageable().getPageSize()).isEqualTo(10);
        Assertions.assertThat(pageLoan.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(pageLoan.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve retornar empréstimos cuja data empréstimo for menor que ou igual a três dias atrás e não retornados.")
    public void findByLoanDateLessThanAndNotReturned() {   // com prazo de vencimento expirado
        //cenario
        Loan loan = createandPersistLoan(LocalDate.now().minusDays(5));

        //execucao
        List<Loan> result = loanRepository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        //verificacao
        Assertions.assertThat(result).hasSize(1).contains(loan);
    }

    @Test
    @DisplayName("Deve retornar vazio quando não houver empréstimos atrasados.")
    public void notFindByLoanDateLessThanAndNotReturned() {
        //cenario
        Loan loan = createandPersistLoan(LocalDate.now());

        //execucao
        List<Loan> result = loanRepository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        //verificacao
        Assertions.assertThat(result).isEmpty();
    }


    private Loan createandPersistLoan(LocalDate loandate) {
        Books book = Books.builder().title("As aventuras").author("Richardson").isbn("9781234567897").build();
        entityManagerTest.persist(book);
        Loan loan = Loan.builder().customerEmail("fulano@email.com").customer("Fulano").book(book).loanDate(loandate).build();
        entityManagerTest.persist(loan);
        return loan;
    }

}
