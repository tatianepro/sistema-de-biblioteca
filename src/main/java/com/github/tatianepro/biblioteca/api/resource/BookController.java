package com.github.tatianepro.biblioteca.api.resource;

import com.github.tatianepro.biblioteca.api.dto.BookDto;
import com.github.tatianepro.biblioteca.api.dto.LoanDto;
import com.github.tatianepro.biblioteca.api.dto.LoanFilterDto;
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

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final ModelMapper modelMapper;
    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto create(@RequestBody @Valid BookDto bookDto) {
        Books entity = modelMapper.map( bookDto, Books.class );
        entity = bookService.save(entity);
        return modelMapper.map( entity, BookDto.class );
    }

    @GetMapping("{id}")
    public BookDto get(@PathVariable Long id) {
        return bookService
                .getById(id)
                .map(book -> modelMapper.map(book, BookDto.class))
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Books book = bookService
                .getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        bookService.delete(book);
    }

    @PutMapping("{id}")
    public BookDto put(@PathVariable Long id, @RequestBody BookDto bookDto) {
        return bookService
                .getById(id)
                .map(book -> {
                    book.setTitle(bookDto.getTitle());
                    book.setAuthor(bookDto.getAuthor());
                    Books updatedBook = bookService.update(book);
                    return modelMapper.map(updatedBook, BookDto.class);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<BookDto> find(BookDto bookDto, Pageable pageRequest) {
        Books filter = modelMapper.map(bookDto, Books.class);
        Page<Books> pageResult = bookService.find(filter, pageRequest);
        List<BookDto> bookListDto = pageResult.getContent()
                .stream()
                .map(book -> modelMapper.map(book, BookDto.class))
                .collect(Collectors.toList());
        return new PageImpl<BookDto>(bookListDto, pageRequest, pageResult.getTotalElements());
    }

    @GetMapping("{id}/loans")
    public Page<LoanFilterDto> getLoansByBook(@PathVariable("id") Long id, Pageable pageRequest) {
        Books book = bookService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Page<Loan> loansByBookPageable = loanService.getLoansByBook(book, pageRequest);
        List<LoanFilterDto> dtoList = loansByBookPageable
                .getContent()
                .stream()
                .map(loan -> {
                    Books loanBook = loan.getBook();
                    BookDto bookDto = modelMapper.map(loanBook, BookDto.class);
                    LoanFilterDto loanDto = modelMapper.map(loan, LoanFilterDto.class);
                    loanDto.setBookDto(bookDto);
                    return loanDto;
                }).collect(Collectors.toList());
        return new PageImpl<LoanFilterDto>(dtoList, pageRequest, loansByBookPageable.getTotalElements());
    }

}
