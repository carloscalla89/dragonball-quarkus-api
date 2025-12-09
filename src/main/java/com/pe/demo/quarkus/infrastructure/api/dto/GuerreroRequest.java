package com.pe.demo.quarkus.infrastructure.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@NoArgsConstructor
@Schema(description = "Modelo para registrar un nuevo guerrero")
public class GuerreroRequest {

    @Schema(description = "Nombre del personaje", example = "Vegeta", required = true)
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @Schema(description = "Raza del personaje", example = "Saiyan", required = true)
    @NotBlank(message = "La raza es obligatoria")
    private String raza;

    @Schema(description = "Nivel de Ki actual", example = "8500", minimum = "1")
    @Positive(message = "El poder debe ser positivo")
    private int nivelPoder;
}
