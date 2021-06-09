package com.github.tatianepro.biblioteca.service;

import com.github.tatianepro.biblioteca.model.entity.Books;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookService {
    Books save(Books book);
    Optional<Books> getById(Long id);
    void delete(Books book);
    Books update(Books book);
    Page<Books> find(Books filter, Pageable pageRequest);
    Optional<Books> getBookByIsbn(String isbn);
}

