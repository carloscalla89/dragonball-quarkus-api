package com.pe.demo.quarkus.domain.exception;

public class BusinessException extends RuntimeException {

    // Podemos guardar metadata extra si queremos
    public BusinessException(String mensaje) {
        super(mensaje);
    }
}
