package com.pe.demo.quarkus.infrastructure.persistence.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Lombok genera Getters, Setters, toString, etc.
@NoArgsConstructor // Necesario para Jackson
@JsonIgnoreProperties(ignoreUnknown = true) // Ignora campos extra que no nos interesan
public class DragonBallCharacterResponse {

    private Long id;
    private String name;
    private String ki;
    private String maxKi;
    private String race;
    private String gender;
    private String description;
    private String image;
}
