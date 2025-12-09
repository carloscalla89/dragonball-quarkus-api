package com.pe.demo.quarkus.infrastructure.api.controller;

import com.pe.demo.quarkus.application.CharacterService;
import com.pe.demo.quarkus.domain.Character;
import com.pe.demo.quarkus.infrastructure.api.dto.DragonballResponse;
import com.pe.demo.quarkus.infrastructure.api.dto.ErrorResponse;
import com.pe.demo.quarkus.infrastructure.api.dto.GuerreroRequest;
import com.pe.demo.quarkus.infrastructure.api.dto.GuerreroResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.Data;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;

@Path("/api/v1/guerreros")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Gestión de Guerreros", description = "Operaciones para crear, buscar y eliminar guerreros Z")
public class GuerreroController {

    private final CharacterService useCase;

    // Inyección por constructor (Best Practice)
    @Inject
    public GuerreroController(CharacterService useCase) {
        this.useCase = useCase;
    }

    // GET: Obtener por ID
    @GET
    @Path("/{id}")
    @Timed(name = "timer_obtener_guerrero",
            description = "Tiempo que toma buscar un guerrero (incluye llamada externa)",
            unit = MetricUnits.MILLISECONDS)
    @Operation(summary = "Buscar guerrero por ID", description = "Consulta la API externa de Dragon Ball para obtener detalles.")
    @APIResponse(responseCode = "200", description = "Guerrero encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GuerreroResponse.class)))
    @APIResponse(responseCode = "404", description = "Guerrero no encontrado en la base de datos externa")
    public Response obtenerGuerrero(@PathParam("id") Long id) {

        return Response.ok(useCase.obtenerInfoGuerrero(id)).build();
    }

    @GET
    @Timed(name = "timer_obtener_guerreros",
            description = "Tiempo que toma obtener todos los guerreros",
            unit = MetricUnits.MILLISECONDS)
    @Operation(summary = "Obtener todos los guerreros paginados",
            description = "Consulta la API externa de Dragon Ball para obtener detalles.")
    @APIResponse(responseCode = "200", description = "Guerreros encontrados",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DragonballResponse.class)))
    @APIResponse(responseCode = "404", description = "Guerrero no encontrado en la base de datos externa")
    public Response obtenerGuerreros(@QueryParam("page") @DefaultValue("1") int page,
                                     @QueryParam("limit") @DefaultValue("10") int limit) {

        return Response.ok(useCase.obtenerTodosLosGuerreros(page, limit)).build();
    }

    // POST: Crear nuevo
    @POST
    @Timed(name = "timer_crear_guerrero",
            description = "Tiempo que toma crear guerreros",
            unit = MetricUnits.MILLISECONDS)
    @Counted(name = "contador_guerreros_creados",
            description = "Cantidad total de guerreros creados exitosamente")
    @Operation(summary = "Registrar nuevo guerrero", description = "Guarda un guerrero en la memoria local si cumple las validaciones.")
    @APIResponse(responseCode = "201", description = "Guerrero creado exitosamente")
    @APIResponse(responseCode = "400", description = "Datos inválidos (ej: nombre vacío o poder negativo)")
    public Response crearGuerrero(GuerreroRequest request) {
        // 1. Mapear Request DTO -> Entidad de Dominio
        Character character = Character
                .builder()
                .id(request.getId())
                .name(request.getNombre())
                .race(request.getRaza())
                .ki(request.getNivelPoder())
                .maxKi(request.getNivelMaxPoder()+"")
                .image(request.getImagen())
                .build();

        // 2. Ejecutar lógica de negocio
        try {
            useCase.registrarNuevoGuerrero(character);

            // 3. Responder 201 Created
            return Response.created(URI.create("/api/v1/characters/" + character.getId())).build();

        } catch (IllegalArgumentException e) {
            // Manejo de errores de negocio (ej: reglas del dominio fallaron)
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ErrorResponse
                            .builder()
                            .tipo("Regla de Negocio")
                            .mensaje(e.getMessage())
                            .status(Response.Status.BAD_REQUEST.getStatusCode())
                            .build()
                    )
                    .build();
        }
    }

    // DELETE: Eliminar
    @DELETE
    @Path("/{nombre}")
    @Operation(summary = "Expulsar guerrero", description = "Elimina un guerrero de la lista local por su nombre exacto.")
    @APIResponse(responseCode = "204", description = "Eliminado correctamente (o no existía)")
    public Response eliminarGuerrero(@PathParam("nombre") String nombre) {
        useCase.expulsarGuerrero(nombre);
        // En REST, un borrado exitoso suele ser 204 No Content
        return Response.noContent().build();
    }

}
