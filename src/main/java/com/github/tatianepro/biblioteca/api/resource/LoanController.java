package com.github.tatianepro.biblioteca.api.resource;

import com.github.tatianepro.biblioteca.api.dto.BookDto;
import com.github.tatianepro.biblioteca.api.dto.LoanDto;
import com.github.tatianepro.biblioteca.api.dto.LoanFilterDto;
import com.github.tatianepro.biblioteca.api.dto.ReturnedLoanDto;
import com.github.tatianepro.biblioteca.model.entity.Books;
import com.github.tatianepro.biblioteca.model.entity.Loan;
import com.github.tatianepro.biblioteca.service.BookService;
import com.github.tatianepro.biblioteca.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final BookService bookService;
    private final LoanService loanService;
    private final ModelMapper modelMapper;

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

    @GetMapping
    public Page<LoanFilterDto> find(LoanFilterDto loanFilterDto, Pageable pageRequest) {

        Loan loanMapped = modelMapper.map(loanFilterDto, Loan.class);

        Page<Loan> loanPage = loanService.find(loanMapped, pageRequest);

        List<LoanFilterDto> loans = loanPage
                .getContent()
                .stream()
                .map( loan -> {
                    Books book = loan.getBook();
                    BookDto bookDto = modelMapper.map(book, BookDto.class);
                    LoanFilterDto loanDto = modelMapper.map(loan, LoanFilterDto.class);
                    loanDto.setBookDto(bookDto);
                    return loanDto;
                }).collect(Collectors.toList());

        return new PageImpl<LoanFilterDto>(loans, pageRequest, loanPage.getTotalElements());
    }

}
