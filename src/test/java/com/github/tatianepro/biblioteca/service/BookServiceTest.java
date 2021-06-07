package com.github.tatianepro.biblioteca.service;

import com.github.tatianepro.biblioteca.model.entity.Books;
import com.github.tatianepro.biblioteca.model.repository.BookRepository;
import com.github.tatianepro.biblioteca.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService bookService;

    @MockBean
    BookRepository bookRepository;

    @BeforeEach
    public void setUp() {
        this.bookService = new BookServiceImpl( bookRepository );
    }

    @Test
    @DisplayName("Deve salvar um livro.")
    public void saveBookTest() {
        //cenario
        Books book = Books.builder().title("As aventuras").author("Artur").isbn("9781234567897").build();
        Mockito.when(bookRepository.save(book)).thenReturn(
                Books.builder().id(1L).title("As aventuras").author("Artur").isbn("9781234567897").build());

        //execucao
        Books savedBook = bookService.save(book);

        //verificacao
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
        assertThat(savedBook.getAuthor()).isEqualTo("Artur");
        assertThat(savedBook.getIsbn()).isEqualTo("9781234567897");
    }

}
