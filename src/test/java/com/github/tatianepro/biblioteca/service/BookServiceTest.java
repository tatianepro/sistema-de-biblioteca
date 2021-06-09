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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por Id quando ele não existe na base de dados.")
    public void bookNotFoundByIdTest() {
        //cenario
        Long id = 1L;

        Mockito.when(bookRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        //execucao
        Optional<Books> bookNotFound = bookService.getById(id);

        //verificacao
        assertThat( bookNotFound.isPresent() ).isFalse();

    }

    @Test
    @DisplayName("Deve deletar um livro.")
    public void deleteTest() {
        //cenario
        Books book = Books.builder().id(1L).build();

        //execucao
        org.junit.jupiter.api.Assertions
                .assertDoesNotThrow( () -> bookService.delete(book));   // verifica que não lançou erro e chamou método delete

        //verificacao
        Mockito.verify(bookRepository, Mockito.times(1)).delete(book);

    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar deletar um livro inexistente.")
    public void deleteInvalidBookTest() {
        //cenario
        Books book = new Books();

        //execucao
        Exception exception = assertThrows(IllegalArgumentException.class, () -> bookService.delete(book));
        String expectedMessage = "Book id cannot be null";
        String actualMessage = exception.getMessage();

        //verificacao
        Mockito.verify(bookRepository, Mockito.never()).delete(book);
        org.junit.jupiter.api.Assertions
                .assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    @DisplayName("Deve atualizar um livro.")
    public void updateBookTest() {
        //cenario
        Long id = 1L;
        Books existingBook = Books.builder().id(1L).build();

        //simulacao
        Books updateBook = createNewBook();
        updateBook.setId(id);
        Mockito.when(bookRepository.save(existingBook)).thenReturn(updateBook);

        //execucao
        Books book = bookService.update(existingBook);

        //verificacao
        assertThat(book.getId()).isEqualTo(updateBook.getId());
        assertThat(book.getTitle()).isEqualTo(updateBook.getTitle());
        assertThat(book.getAuthor()).isEqualTo(updateBook.getAuthor());
        assertThat(book.getIsbn()).isEqualTo(updateBook.getIsbn());
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar atualizar um livro inexistente.")
    public void updateInvalidBookTest() {
        //cenario
        Books book = new Books();

        //execucao
        assertThrows(IllegalArgumentException.class, () -> bookService.update(book));

        //verificacao
        Mockito.verify(bookRepository, Mockito.never()).delete(book);

    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBookTest() {
        //cenario
        Long id = 1L;
        Books book = createNewBook();

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Books> booksList = Arrays.asList(book);
        Page<Books> booksPage = new PageImpl<Books>(booksList, pageRequest, 1);
        Mockito.when(bookRepository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))).thenReturn(booksPage);

        //execucao
        Page<Books> result = bookService.find(book, pageRequest);

        //verificacao
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(booksList);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }


    private Books createNewBook() {
        return Books.builder().title("As aventuras").author("Artur").isbn("9781234567897").build();
    }

}
