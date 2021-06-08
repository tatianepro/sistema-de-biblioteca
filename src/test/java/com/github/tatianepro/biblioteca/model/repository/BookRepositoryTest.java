package com.github.tatianepro.biblioteca.model.repository;

import com.github.tatianepro.biblioteca.model.entity.Books;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// teste de integração
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest    // creates and drops a memory database instance
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManagerTest;

    @Autowired
    BookRepository bookRepository;

    @Test
    @DisplayName("Deve retornar true quando existir um livro na base de dados com o ISBN informado.")
    public void returnTrueWhenIsbnExists() {

        //cenario
        String isbn = "9781234567897";
        Books book = Books.builder().title("As aventuras").author("Fulano").isbn(isbn).build();
        entityManagerTest.persist(book);

        //execucao
        boolean exists = bookRepository.existsByIsbn(isbn);

        //verificacao
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando não existit um livro na base de dados com o ISBN informado.")
    public void returnFalseWhenIsbnDoesntExists() {
        //cenario
        String isbn1 = "9781234567897";
        String isbn2 = "9781234567898";
        Books book = Books.builder().title("As aventuras").author("Fulano").isbn(isbn1).build();
        entityManagerTest.persist(book);

        //execucao
        boolean exists = bookRepository.existsByIsbn(isbn2);

        //verificacao
        Assertions.assertThat(exists).isFalse();
    }

}
