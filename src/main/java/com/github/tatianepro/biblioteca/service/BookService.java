package com.github.tatianepro.biblioteca.service;

import com.github.tatianepro.biblioteca.model.entity.Books;

import java.util.Optional;

public interface BookService {
    Books save(Books book);
    Optional<Books> getById(Long id);
}

