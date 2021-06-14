package com.github.tatianepro.biblioteca.api.resource;

import com.github.tatianepro.biblioteca.api.dto.BookDto;
import com.github.tatianepro.biblioteca.api.dto.LoanFilterDto;
import com.github.tatianepro.biblioteca.model.entity.Books;
import com.github.tatianepro.biblioteca.model.entity.Loan;
import com.github.tatianepro.biblioteca.service.BookService;
import com.github.tatianepro.biblioteca.service.LoanService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Api(tags = {"Book Api"})
@SwaggerDefinition(tags = {
        @Tag(name = "Book Api", description = "resource for Biblioteca API")
})
@Slf4j
public class BookController {

    private final BookService bookService;
    private final ModelMapper modelMapper;
    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Creates a book")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Book successfully created"),
            @ApiResponse(code = 400, message = "Invalid argument")
    })
    public BookDto create(@RequestBody @Valid BookDto bookDto) {
        log.info(" -----> creating a book for isbn {}", bookDto.getIsbn());
        Books entity = modelMapper.map( bookDto, Books.class );
        entity = bookService.save(entity);
        return modelMapper.map( entity, BookDto.class );
    }

    @GetMapping("{id}")
    @ApiOperation("Gets a book detail by ID ")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Book was found"),
            @ApiResponse(code = 400, message = "Invalid argument"),
            @ApiResponse(code = 404, message = "Book not found")
    })
    public BookDto get(@PathVariable Long id) {
        log.info(" -----> obtaining book detail for id {}", id);
        return bookService
                .getById(id)
                .map(book -> modelMapper.map(book, BookDto.class))
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Deletes a book by ID")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Book successfully deleted"),
            @ApiResponse(code = 404, message = "Book not found")
    })
    public void delete(@PathVariable Long id) {
        log.info(" -----> deleting a book of id {}", id);
        Books book = bookService
                .getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        bookService.delete(book);
    }

    @PutMapping("{id}")
    @ApiOperation("Updates a book by ID and its properties")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Book successfully updated"),
            @ApiResponse(code = 404, message = "Book not found")
    })
    public BookDto put(@PathVariable Long id, @RequestBody @Valid BookDto bookDto) {
        log.info(" -----> updating the book details for id {}", id);
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
    @ApiOperation("Finds a book list by parameters (optionally by page parameters)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Book was found")
    })
    public Page<BookDto> find(BookDto bookDto, Pageable pageRequest) {
        log.info(" -----> searching for books of isbn {}", bookDto.getIsbn());
        Books filter = modelMapper.map(bookDto, Books.class);
        Page<Books> pageResult = bookService.find(filter, pageRequest);
        List<BookDto> bookListDto = pageResult.getContent()
                .stream()
                .map(book -> modelMapper.map(book, BookDto.class))
                .collect(Collectors.toList());
        return new PageImpl<BookDto>(bookListDto, pageRequest, pageResult.getTotalElements());
    }

    @GetMapping("{id}/loans")
    @ApiOperation("Finds a borrowed book list by ID (optionally by page parameters)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Book was found"),
            @ApiResponse(code = 404, message = "Book not found")
    })
    public Page<LoanFilterDto> getLoansByBook(@PathVariable("id") Long id, Pageable pageRequest) {
        log.info(" -----> searching for borrowed book for id {}", id);
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
