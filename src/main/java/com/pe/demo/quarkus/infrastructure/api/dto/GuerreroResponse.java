package com.pe.demo.quarkus.infrastructure.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GuerreroResponse {
    private Long id;
    private String nombre;
    private String descripcion; // Campo extra calculado para la vista
    private String ki;
    private String kiMax;
    private String imagen;
    private String origen;
}
