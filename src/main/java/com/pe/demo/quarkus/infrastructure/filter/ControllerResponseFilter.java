package com.pe.demo.quarkus.infrastructure.filter;

import io.opentelemetry.api.trace.Span;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@Provider
public class ControllerResponseFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext containerRequestContext,
                       ContainerResponseContext containerResponseContext) throws IOException {

        // 1. Inspeccionar la solicitud original (si es necesario)
        String method = containerRequestContext.getMethod();

        // 2. Obtener el estado y el contenido del Response generado por el Controller
        int status = containerResponseContext.getStatus();
        // a) Agregar un Header de metadatos o seguridad
        containerResponseContext.getHeaders().add("X-Server-Processed", "true");
        containerResponseContext.getHeaders().add("X-Request-Path",
                containerRequestContext.getUriInfo().getPath());
        containerResponseContext.getHeaders().add("x-trace-id", Span.current().getSpanContext().getTraceId());

        log.info("[END] - response end: status:{}", status);

    }
}
