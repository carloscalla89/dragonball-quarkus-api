package com.pe.demo.quarkus.infrastructure.persistence.repository;

import com.pe.demo.quarkus.infrastructure.persistence.dto.DragonBallCharacterResponse;
import com.pe.demo.quarkus.infrastructure.persistence.dto.DragonBallPageResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
// 'configKey' es el enlace con application.properties
public interface DragonBallRestClientDefinition {

    @GET
    @Path("/characters/{id}")
    DragonBallCharacterResponse getCharacterById(@PathParam("id") Long id);

    @GET
    @Path("/characters")
    DragonBallPageResponse getAll(@QueryParam("page") int page, @QueryParam("limit") int limit);
}
