package com.pe.demo.quarkus.infrastructure.persistence.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DragonBallPageResponse {

    // La API pone la lista real dentro de este campo "items"
    private List<DragonBallCharacterResponse> items;
}
