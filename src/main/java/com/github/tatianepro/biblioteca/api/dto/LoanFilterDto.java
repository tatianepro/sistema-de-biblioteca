package com.github.tatianepro.biblioteca.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LoanFilterDto {
    private Long id;
    private String isbn;
    private String customer;
    private BookDto bookDto;
}
