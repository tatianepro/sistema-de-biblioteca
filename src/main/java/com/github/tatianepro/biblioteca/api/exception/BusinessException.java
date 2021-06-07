package com.github.tatianepro.biblioteca.api.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String mensagemErro) {
        super(mensagemErro);
    }
}
