package com.github.tatianepro.biblioteca.model.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Books {

    private Long id;
    private String title;
    private String author;
    private String isbn;

}
