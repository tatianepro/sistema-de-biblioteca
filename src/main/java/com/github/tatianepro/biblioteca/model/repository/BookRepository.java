package com.github.tatianepro.biblioteca.model.repository;

import com.github.tatianepro.biblioteca.model.entity.Books;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Books, Long> {
    boolean existsByIsbn(String isbn);
    Optional<Books> findByIsbn(String isbn);
}
