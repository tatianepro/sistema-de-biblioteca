package com.github.tatianepro.biblioteca.api.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BookDto {

    private Long id;
    private String title;
    private String author;
    private String isbn;

}
