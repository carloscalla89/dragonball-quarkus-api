package com.pe.demo.quarkus.infrastructure.input.api.controller;

import com.pe.demo.quarkus.application.CharacterService;
import com.pe.demo.quarkus.infrastructure.input.api.dto.GuerreroResponse;
import io.micrometer.core.annotation.Timed;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Slf4j
@Path("/api/v1/guerreros/reactive")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Gestión de Guerreros", description = "Operaciones para crear, buscar y eliminar guerreros Z")
public class GuerreroReactiveController {

    private final CharacterService useCase;

    // Inyección por constructor (Best Practice)
    @Inject
    public GuerreroReactiveController(CharacterService useCase) {
        this.useCase = useCase;
    }

    // GET: Obtener por ID
    @GET
    @Path("/{id}")
    @Timed(value = "timer_obtener_guerrero_reactive",
            description = "Tiempo que toma buscar un guerrero (incluye llamada externa)",
            histogram = true)
    @Operation(summary = "Buscar guerrero por ID", description = "Consulta la API externa de Dragon Ball para obtener detalles.")
    @APIResponse(responseCode = "200", description = "Guerrero encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GuerreroResponse.class)))
    @APIResponse(responseCode = "404", description = "Guerrero no encontrado en la base de datos externa")
    public Uni<Response> obtenerGuerrero(@PathParam("id") Long id) {

        return useCase
                .obtenerInfoGuerreroReactivo(id)
                .onItem()
                .transform(dto -> {
                    if (dto.getData() == null) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    }
                    return Response.ok(dto).build();
                })
                // Manejo de errores reactivo (try-catch visual)
                .onFailure().recoverWithItem(error ->
                        Response.status(500).entity(error.getMessage()).build()
                );




    }
}
