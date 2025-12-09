package com.pe.demo.quarkus.infrastructure.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private String tipo;    // Ej: "Error de Negocio", "Error Interno"
    private String mensaje; // Ej: "El guerrero ya existe"
    private int status;     // Ej: 400, 404, 500
    private String fecha;   // Timestamp
}