package com.pe.demo.quarkus.cross;

import io.opentelemetry.api.trace.Span;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@Provider
public class IncomingRequestFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        String method = containerRequestContext.getMethod();
        String path = containerRequestContext.getUriInfo().getAbsolutePath().toString();

        // para leer headers
        String correlationId = containerRequestContext.getHeaderString("X-Correlation-ID");

        // 1. Obtener el Span actual (puede ser el que creó Quarkus automáticamente al recibir la petición HTTP)
        Span currentSpan = Span.current();

        // 2. Extraer el Trace ID
        String traceId = currentSpan.getSpanContext().getTraceId();
        String spanId = currentSpan.getSpanContext().getSpanId();

        log.info("[START] - request init: traceId:{}, spanId:{}, method:{}, path:{}", traceId, spanId, method, path);

    }
}
