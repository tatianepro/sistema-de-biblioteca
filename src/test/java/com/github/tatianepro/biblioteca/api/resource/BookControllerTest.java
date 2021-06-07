package com.github.tatianepro.biblioteca.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tatianepro.biblioteca.api.dto.BookDto;
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

        BookDto bookDto = BookDto.builder().title("As aventuras").author("Artur").isbn("9781234567897").build();
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
                .andExpect( MockMvcResultMatchers.status().isCreated() )
                .andExpect( MockMvcResultMatchers.jsonPath("id").value(10L) )
                .andExpect( MockMvcResultMatchers.jsonPath("title").value("As aventuras") )
                .andExpect( MockMvcResultMatchers.jsonPath("author").value("Artur") )
                .andExpect( MockMvcResultMatchers.jsonPath("isbn").value("9781234567897") )
                ;

    }

    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficientes para criação de um livro.")
    public void createInvalidBookTest() {

    }

}
