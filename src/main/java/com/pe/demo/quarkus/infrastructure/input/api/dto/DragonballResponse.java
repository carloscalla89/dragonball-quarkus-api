package com.pe.demo.quarkus.infrastructure.input.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DragonballResponse<T> {

    private List<T> elementos;
    private T data;
    private Integer paginaActual;
    private Integer totalElementos;
    private Integer totalElementosPorPagina;

}
