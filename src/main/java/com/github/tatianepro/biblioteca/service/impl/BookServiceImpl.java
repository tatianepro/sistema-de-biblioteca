package com.github.tatianepro.biblioteca.service.impl;

import com.github.tatianepro.biblioteca.api.exception.BusinessException;
import com.github.tatianepro.biblioteca.model.entity.Books;
import com.github.tatianepro.biblioteca.model.repository.BookRepository;
import com.github.tatianepro.biblioteca.service.BookService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Books save(Books book) {
        if (repository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("Isbn já cadastrado.");
        }
        return this.repository.save(book);
    }

    @Override
    public Optional<Books> getById(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public void delete(Books book) {
        if (book == null || book.getId() == null) {
            throw new IllegalArgumentException("Book id cannot be null");
        }
        this.repository.delete(book);
    }

    @Override
    public Books update(Books book) {
        return null;
    }
}
