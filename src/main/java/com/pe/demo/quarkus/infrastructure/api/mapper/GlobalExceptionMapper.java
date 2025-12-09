package com.pe.demo.quarkus.infrastructure.api.mapper;

import com.pe.demo.quarkus.infrastructure.api.dto.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {

        // 1. Logueamos el error real en el servidor (para que tú lo veas)
        LOGGER.error("Error no controlado capturado: ", exception);

        // 2. Preparamos una respuesta genérica para el cliente (por seguridad)
        ErrorResponse error = ErrorResponse.builder()
                .tipo("Error Interno del Servidor")
                .mensaje("Ha ocurrido un error inesperado, contacte al admin.")
                .status(500)
                .fecha(LocalDateTime.now().toString())
                .build();

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .build();
    }
}
