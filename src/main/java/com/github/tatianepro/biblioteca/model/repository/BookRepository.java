package com.github.tatianepro.biblioteca.model.repository;

import com.github.tatianepro.biblioteca.model.entity.Books;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Books, Long> {
}
