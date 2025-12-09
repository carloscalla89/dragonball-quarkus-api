package com.pe.demo.quarkus.infrastructure.api.mapper;

import com.pe.demo.quarkus.domain.exception.BusinessException;
import com.pe.demo.quarkus.infrastructure.api.dto.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;

@Provider // <--- OBLIGATORIO: Esto registra la clase en Quarkus
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    @Override
    public Response toResponse(BusinessException exception) {

        ErrorResponse error = ErrorResponse.builder()
                .tipo("Regla de Negocio")
                .mensaje(exception.getMessage())
                .status(400) // Asumimos Bad Request por defecto para reglas de negocio
                .fecha(LocalDateTime.now().toString())
                .build();

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(error)
                .build();
    }
}
