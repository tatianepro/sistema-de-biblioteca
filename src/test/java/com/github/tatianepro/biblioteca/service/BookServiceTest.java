package com.github.tatianepro.biblioteca.service;

import com.github.tatianepro.biblioteca.api.exception.BusinessException;
import com.github.tatianepro.biblioteca.model.entity.Books;
import com.github.tatianepro.biblioteca.model.repository.BookRepository;
import com.github.tatianepro.biblioteca.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

//teste de regra de negócio
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
        Books book = createNewBook();
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(false);
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

    @Test
    @DisplayName("Deve lançar erro de negócio ao tentar salvar um livro com ISBN duplicado")
    public void shouldNotSaveABookWithDuplicatedISBN() {
        //cenario
        Books book = createNewBook();
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //execucao
        Throwable exception = Assertions.catchThrowable(() -> bookService.save(book));

        //verificacao
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado.");

        Mockito.verify(bookRepository, Mockito.never()).save(book); // simula que o repository nunca vai chamar o método save()
    }

    @Test
    @DisplayName("Deve obter um livro por id")
    public void getByIdTest() {
        //cenario
        Long id = 1L;
        Books book = createNewBook();
        book.setId(id);

        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        //execucao
        Optional<Books> bookFound = bookService.getById(id);

        //verificacao
        assertThat( bookFound.isPresent() ).isTrue();
        assertThat( bookFound.get().getId() ).isEqualTo(id);
        assertThat( bookFound.get().getTitle() ).isEqualTo(createNewBook().getTitle());
        assertThat( bookFound.get().getAuthor() ).isEqualTo(createNewBook().getAuthor());
        assertThat( bookFound.get().getIsbn() ).isEqualTo(createNewBook().getIsbn());
    }

    private Books createNewBook() {
        return Books.builder().title("As aventuras").author("Artur").isbn("9781234567897").build();
    }

}
