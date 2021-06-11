package com.github.tatianepro.biblioteca.model.repository;

import com.github.tatianepro.biblioteca.model.entity.Books;
import com.github.tatianepro.biblioteca.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    @Query("select case when ( count(l.id) > 0 ) then true else false end " +
            " from Loan l where l.book = :book and ( l.returned is null or l.returned is false )")
    Boolean existsByBookAndNotReturned(@Param("book") Books book);

    @Query("select l from Loan as l join l.book as b where b.isbn = :isbn or l.customer = :customer")
    Page<Loan> findByBookIsbnOrCustomer(@Param("isbn") String isbn, @Param("customer") String customerName, Pageable pageRequest);

    Page<Loan> findByBook(Books book, Pageable pageRequest);

    @Query("select l from Loan l where l.loanDate <= :threeDaysAgo and (l.returned is null or l.returned is false)")
    List<Loan> findByLoanDateLessThanAndNotReturned(@Param("threeDaysAgo") LocalDate threeDaysAgo);
}

