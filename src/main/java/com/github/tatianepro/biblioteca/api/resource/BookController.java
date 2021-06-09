package com.github.tatianepro.biblioteca.api.resource;

import com.github.tatianepro.biblioteca.api.dto.BookDto;
import com.github.tatianepro.biblioteca.model.entity.Books;
import com.github.tatianepro.biblioteca.service.BookService;
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

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService bookService;
    private ModelMapper modelMapper;

    public BookController(BookService bookService, ModelMapper modelMapper) {
        this.bookService = bookService;
        this.modelMapper = modelMapper;
    }

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

}
