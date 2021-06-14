package com.github.tatianepro.biblioteca.api.resource;

import com.github.tatianepro.biblioteca.api.dto.BookDto;
import com.github.tatianepro.biblioteca.api.dto.LoanDto;
import com.github.tatianepro.biblioteca.api.dto.LoanFilterDto;
import com.github.tatianepro.biblioteca.api.dto.ReturnedLoanDto;
import com.github.tatianepro.biblioteca.api.exception.BusinessException;
import com.github.tatianepro.biblioteca.model.entity.Books;
import com.github.tatianepro.biblioteca.model.entity.Loan;
import com.github.tatianepro.biblioteca.service.BookService;
import com.github.tatianepro.biblioteca.service.LoanService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Api(tags = {"Loan Api"})
@SwaggerDefinition(tags = {
        @Tag(name = "Loan Api", description = "resource for Biblioteca API")
})
public class LoanController {

    private final BookService bookService;
    private final LoanService loanService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Creates a loan from a valid book isbn")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Loan successfully created"),
            @ApiResponse(code = 400, message = "Invalid argument")
    })
    public Long create(@RequestBody @Valid LoanDto loanDto) {

        Books book = bookService
                .getBookByIsbn(loanDto.getIsbn())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));
        Loan loan = Loan
                .builder()
                .book(book)
                .customer(loanDto.getCustomer())
                .customerEmail(loanDto.getEmail())
                .loanDate(LocalDate.now())
                .build();
        Loan loanSaved = loanService.save(loan);
        return loanSaved.getId();
    }

    @PatchMapping("/{id}")
    @ApiOperation("Updates the loan status with true or false")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Loan status successfully updated"),
            @ApiResponse(code = 400, message = "Invalid argument"),
            @ApiResponse(code = 404, message = "Loan not found")
    })
    public void returnBook(@PathVariable Long id, @RequestBody @Valid ReturnedLoanDto returnedLoanDto) {
        if (returnedLoanDto.getReturned() == null) {
            throw new BusinessException("Value must be true or false");
        }
        Loan loanFound = loanService
                .getById(id)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        returnedLoanDto.setReturned(returnedLoanDto.getReturned());
        loanService.update(loanFound);
    }

    @GetMapping
    @ApiOperation("Finds a loan book list")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Book was found"),
            @ApiResponse(code = 400, message = "Invalid argument")
    })
    public Page<LoanFilterDto> find(LoanFilterDto loanFilterDto, Pageable pageRequest) {
        if (loanFilterDto.getIsbn() == null) {
            throw new BusinessException("Must fill the isbn field");
        }
        BookDto bookFilterDto = getBookDto(loanFilterDto);
        loanFilterDto.setBookDto(bookFilterDto);

        Loan loanMapped = modelMapper.map(loanFilterDto, Loan.class);

        Page<Loan> loanPage = loanService.find(loanMapped, pageRequest);

        List<LoanFilterDto> loans = loanPage
                .getContent()
                .stream()
                .map( loan -> {
                    Books book = loan.getBook();
                    BookDto mappedBookDto = modelMapper.map(book, BookDto.class);
                    LoanFilterDto mappedLoanDto = modelMapper.map(loan, LoanFilterDto.class);
                    mappedLoanDto.setBookDto(mappedBookDto);
                    return mappedLoanDto;
                }).collect(Collectors.toList());

        return new PageImpl<LoanFilterDto>(loans, pageRequest, loanPage.getTotalElements());
    }

    private BookDto getBookDto(LoanFilterDto loanFilterDto) {
        return BookDto.builder().isbn(loanFilterDto.getIsbn()).build();
    }

}
