package com.github.tatianepro.biblioteca.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Loan {

    private Long id;
    private String customer;
    private Books book;
    private LocalDate loanDate;
    private Boolean returned;

}
