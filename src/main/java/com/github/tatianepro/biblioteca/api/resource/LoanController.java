package com.github.tatianepro.biblioteca.api.resource;

import com.github.tatianepro.biblioteca.api.dto.LoanDto;
import com.github.tatianepro.biblioteca.api.dto.ReturnedLoanDto;
import com.github.tatianepro.biblioteca.model.entity.Books;
import com.github.tatianepro.biblioteca.model.entity.Loan;
import com.github.tatianepro.biblioteca.service.BookService;
import com.github.tatianepro.biblioteca.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final BookService bookService;
    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDto loanDto) {

        Books book = bookService
                .getBookByIsbn(loanDto.getIsbn())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));
        Loan loan = Loan
                .builder()
                .book(book)
                .customer(loanDto.getCustomer())
                .loanDate(LocalDate.now())
                .build();
        Loan loanSaved = loanService.save(loan);
        return loanSaved.getId();
    }

    @PatchMapping("/{id}")
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDto returnedLoanDto) {
        Loan loanFound = loanService
                .getById(id)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        returnedLoanDto.setReturned(returnedLoanDto.getReturned());
        loanService.update(loanFound);
    }

}
