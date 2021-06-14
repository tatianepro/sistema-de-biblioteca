package com.github.tatianepro.biblioteca.api.dto;

import com.github.tatianepro.biblioteca.api.validation.TrueOrFalse;
import jdk.jfr.BooleanFlag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReturnedLoanDto {
    @NotNull
    @TrueOrFalse
    private Boolean returned;
}
