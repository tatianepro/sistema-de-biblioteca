package com.github.tatianepro.biblioteca.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tatianepro.biblioteca.api.dto.BookDto;
import com.github.tatianepro.biblioteca.api.exception.BusinessException;
import com.github.tatianepro.biblioteca.model.entity.Books;
import com.github.tatianepro.biblioteca.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.awt.print.Book;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//teste de design de interacao com usuario
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService bookService;

    @Test
    @DisplayName("Deve criar um livro com sucesso.")
    public void createBookTest() throws Exception {

        BookDto bookDto = createNewBookDto();
        Books savedBook = Books.builder().id(10L).title("As aventuras").author("Artur").isbn("9781234567897").build();

        BDDMockito.given(bookService.save(Mockito.any(Books.class))).willReturn(savedBook);

        String json = new ObjectMapper().writeValueAsString(bookDto);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc
                .perform(mockRequest)
                .andExpect( status().isCreated() )
                .andExpect( MockMvcResultMatchers.jsonPath("id").value(10L) )
                .andExpect( MockMvcResultMatchers.jsonPath("title").value("As aventuras") )
                .andExpect( MockMvcResultMatchers.jsonPath("author").value("Artur") )
                .andExpect( MockMvcResultMatchers.jsonPath("isbn").value("9781234567897") )
                ;

    }

    // validação de integridade
    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficientes para criação de um livro.")
    public void createInvalidBookTest() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new BookDto()); // instancia nova instância nula

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(mockRequest)
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath( "errors", hasSize(3) ) );

    }

    // validação de regra de negócio
    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já utilizado por outro.")
    public void createBookWithoutDuplicatedIsbnTest() throws Exception {
        BookDto bookDto = createNewBookDto();
        String json = new ObjectMapper().writeValueAsString(bookDto);

        String mensagemErro = "Isbn já cadastrado.";
        BDDMockito.given(bookService.save(Mockito.any(Books.class)))
                .willThrow(new BusinessException(mensagemErro));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc
                .perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(mensagemErro));


    }

    @Test
    @DisplayName("Deve obter informações de um livro.")
    public void getBookDetailsTest() throws Exception {
        //cenario
        Long id = 1L;

        Books book = Books
                .builder()
                .id(id)
                .title(createNewBookDto().getTitle())
                .author(createNewBookDto().getAuthor())
                .isbn(createNewBookDto().getIsbn())
                .build();

        BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(book));

        //execucao
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        //verificacao
        mockMvc
                .perform(mockRequest)
                .andExpect( status().isOk() )
                .andExpect( jsonPath("id").value(id) )
                .andExpect( jsonPath("title").value(createNewBookDto().getTitle()) )
                .andExpect( jsonPath("author").value(createNewBookDto().getAuthor()) )
                .andExpect( jsonPath("isbn").value(createNewBookDto().getIsbn()) )
                ;
    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro procurado não existir.")
    public void bookNotFoundTest() throws Exception {
        //cenario
        Long id = 1L;
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        //execucao
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        //verificacao
        mockMvc
                .perform(mockRequest)
                .andExpect( status().isNotFound() );

    }

    @Test
    @DisplayName("Deve deletar um livro.")
    public void deleteBookTest() throws Exception {
        //cenario
        Long id = 1L;

        //execucao
        BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(Books.builder().id(id).build()));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + id));

        //verificacao
        mockMvc
                .perform(mockRequest)
                .andExpect( status().isNoContent() );

    }

    private BookDto createNewBookDto() {
        return BookDto.builder().title("As aventuras").author("Artur").isbn("9781234567897").build();
    }
}
