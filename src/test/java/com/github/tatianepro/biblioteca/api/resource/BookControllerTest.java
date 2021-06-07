package com.github.tatianepro.biblioteca.api.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tatianepro.biblioteca.api.dto.BookDto;
import com.github.tatianepro.biblioteca.api.exception.BusinessException;
import com.github.tatianepro.biblioteca.model.entity.Books;
import com.github.tatianepro.biblioteca.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    public void createBookWithoutDuplicatedIsbn() throws Exception {
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

    private BookDto createNewBookDto() {
        return BookDto.builder().title("As aventuras").author("Artur").isbn("9781234567897").build();
    }
}
