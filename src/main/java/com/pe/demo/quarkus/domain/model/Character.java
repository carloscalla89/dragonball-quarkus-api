package com.pe.demo.quarkus.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class Character {

    private Long id;
    private String name;
    private String race;
    private int ki;
    private String maxKi;
    private String image;
    private String source;
    private String affiliation;
    // Lógica de Negocio REAL: El dominio decide si es fuerte, no el controlador.
    public boolean isPowerful() {
        return this.ki > 9000;
    }
}
