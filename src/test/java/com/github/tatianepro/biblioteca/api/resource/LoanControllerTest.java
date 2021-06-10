package com.github.tatianepro.biblioteca.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tatianepro.biblioteca.api.dto.LoanDto;
import com.github.tatianepro.biblioteca.api.dto.ReturnedLoanDto;
import com.github.tatianepro.biblioteca.api.exception.BusinessException;
import com.github.tatianepro.biblioteca.model.entity.Books;
import com.github.tatianepro.biblioteca.model.entity.Loan;
import com.github.tatianepro.biblioteca.service.BookService;
import com.github.tatianepro.biblioteca.service.LoanService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mockMvc;       // para fazer as requisições

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;

    @Test
    @DisplayName("Deve realizar um emprestimo.")
    public void createLoantest() throws Exception {
        //cenario
        LoanDto loanDto = LoanDto.builder().isbn("9781234567897").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(loanDto);

        Books book = Books.builder().id(1L).isbn("9781234567897").build();
        BDDMockito.given(bookService.getBookByIsbn("9781234567897")).willReturn(Optional.of(book));

        Loan loan = Loan.builder().id(1L).customer("Fulano").book(book).loanDate(LocalDate.now()).build();
        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        //execucao
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        //verificacao
        mockMvc
                .perform(mockRequest)
                .andExpect( status().isCreated() )
                .andExpect( content().string("1") );

    }

    @Test
    @DisplayName("Deve retornar erro ao tentar fazer empréstimo de um livro inexistente.")
    public void invalidIsbnCreateLoanTest() throws Exception {
        //cenario
        LoanDto loanDto = LoanDto.builder().isbn("9781234567897").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(loanDto);

        BDDMockito.given(bookService.getBookByIsbn("9781234567897")).willReturn(Optional.empty());

        //execucao
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        //verificacao
        mockMvc
                .perform(mockRequest)
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath("errors", Matchers.hasSize(1)))
                .andExpect( jsonPath("errors[0]").value("Book not found for passed isbn"));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar fazer empréstimo de um livro emprestado.")
    public void loanedBookErrorOnCreateLoanTest() throws Exception {
        //cenario
        LoanDto loanDto = LoanDto.builder().isbn("9781234567897").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(loanDto);

        Books book = Books.builder().id(1L).isbn("9781234567897").build();
        BDDMockito.given(bookService.getBookByIsbn("9781234567897")).willReturn(Optional.of(book));

        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willThrow(new BusinessException("Book already borrowed."));

        //execucao
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        //verificacao
        mockMvc
                .perform(mockRequest)
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath("errors", Matchers.hasSize(1)))
                .andExpect( jsonPath("errors[0]").value("Book already borrowed."));
    }

    @Test
    @DisplayName("Deve retornar um livro.")
    public void returnLoanBookTest() throws Exception {
        //cenario
        Long id = 1L;
        ReturnedLoanDto returnedLoanDto = ReturnedLoanDto.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(returnedLoanDto);
        Loan loan = Loan.builder().id(id).build();

        BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.of(loan));

        //execucao e verificacao
        mockMvc
                .perform(MockMvcRequestBuilders
                        .patch(LOAN_API.concat("/" + id))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                ).andExpect( status().isOk() );

        Mockito.verify(loanService, Mockito.times(1)).update(loan);
    }

    @Test
    @DisplayName("Deve retornar 404 quando tentar devolver um livro inexistente.")
    public void returnInexistentBookTest() throws Exception {
        //cenario
        Long id = 1L;
        ReturnedLoanDto dto = ReturnedLoanDto.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(loanService.getById(id)).willReturn(Optional.empty());

        //execucao e verificacao
        mockMvc
                .perform(MockMvcRequestBuilders
                        .patch(LOAN_API.concat("/" + id))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                ).andExpect( status().isNotFound() );

    }

    @Test
    @DisplayName("Deve filtrar por empréstimos de livros.")
    public void findBooksLoanTest() throws Exception {
        //cenario
        Long id = 1L;
        Loan loan = creatingLoan();
        loan.setId(id);

        BDDMockito.given(loanService.find(Mockito.any(Loan.class), Mockito.any(Pageable.class)))
                .willReturn( new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0, 10), 1));

        String query = String.format("?isbn=%s&customer=%s&page=0&size=10", loan.getBook().getIsbn(), loan.getCustomer());

        //execucao
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(LOAN_API.concat("/" + query))
                .accept(MediaType.APPLICATION_JSON);

        //verificacao
        mockMvc
                .perform(mockRequest)
                .andExpect( status().isOk() )
                .andExpect( jsonPath("content", Matchers.hasSize(1)));

    }

    private Loan creatingLoan() {
        Books book = Books.builder().id(1L).isbn("9781234567897").build();
        String customerName = "Fulano";

        Loan savingLoan = Loan.builder().customer(customerName).book(book).loanDate(LocalDate.now()).build();
        return savingLoan;
    }

}
