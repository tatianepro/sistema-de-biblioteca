package com.github.tatianepro.biblioteca.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
public class Loan {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100)
    private String customer;
    @JoinColumn(name = "id_book")
    @ManyToOne
    private Books book;
    private LocalDate loanDate;
    private Boolean returned;

}
