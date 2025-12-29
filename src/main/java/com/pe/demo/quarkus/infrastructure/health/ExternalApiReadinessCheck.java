package com.pe.demo.quarkus.infrastructure.health;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Startup
@Readiness // <--- Marca esto como check de "Readiness"
@ApplicationScoped
public class ExternalApiReadinessCheck implements HealthCheck {

    private static final String API_URL = "https://dragonball-api.com";
    //private final MeterRegistry registry;

    // 1. Creamos una variable atómica para guardar el estado (0 o 1)
    private final AtomicInteger connectionStatus = new AtomicInteger(0);

    @Inject
    public ExternalApiReadinessCheck(MeterRegistry registry) {

        // Log para verificar que estás usando la versión nueva
        Log.info(">>> INICIANDO METRICA DEPENDENCY_STATUS <<<");

        // 2. REGISTRAMOS EL GAUGE UNA SOLA VEZ EN EL CONSTRUCTOR
        // Le decimos a Micrometer: "Vigila la variable 'connectionStatus' y reporta su valor"
        Gauge.builder("dependency_status", connectionStatus, AtomicInteger::get)
                .description("Estado de conectividad con APIs externas")
                .tag("name", "dragonball-api")
                .tag("type", "external-api")
                .register(registry);
    }

    @Override
    public HealthCheckResponse call() {
        String host = "dragonball-api.com";
        int port = 443;
        int timeout = 2000;

        boolean isUp = false; // Estado por defecto

        try {
            URL url = new URL(API_URL);
            host = url.getHost();
            port = url.getPort() != -1 ? url.getPort() : (url.getProtocol().equals("http") ? 80 : 443);

            boolean reachable = isHostReachable(host, port, timeout);

            // 3. SOLO ACTUALIZAMOS EL VALOR DE LA VARIABLE
            // Micrometer leerá este valor automáticamente cuando Prometheus pregunte
            connectionStatus.set(reachable ? 1 : 0);

            if (reachable) {
                return HealthCheckResponse.up("DragonBall API Network Reachable");
            } else {
                return HealthCheckResponse.down("DragonBall API Network Unreachable");
            }
        } catch (Exception e) {
            connectionStatus.set(0); // Error = DOWN
            return HealthCheckResponse.down("DragonBall API Check Error: " + e.getMessage());
        }
    }

    private boolean isHostReachable(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            Log.error("⚠️ Fallo de conexión TCP a " + host + ": " + e.getMessage());
            return false;
        }
    }
}