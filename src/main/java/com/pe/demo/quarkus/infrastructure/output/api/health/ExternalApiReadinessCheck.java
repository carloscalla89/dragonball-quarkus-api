package com.pe.demo.quarkus.infrastructure.output.api.health;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

@Readiness // <--- Marca esto como check de "Readiness"
@ApplicationScoped
public class ExternalApiReadinessCheck implements HealthCheck {

    private static final String API_URL = "https://dragonball-api.com";

    @Override
    public HealthCheckResponse call() {
        String host = "dragonball-api.com";
        int port = 443; // Puerto HTTPS por defecto
        int timeout = 2000; // 2 segundos máximo de espera

        try {
            // Intentamos parsear la URL por si acaso cambias la constante arriba
            URL url = new URL(API_URL);
            host = url.getHost();
            // Si la URL tiene puerto explícito lo usamos, si no, asumimos 443 (HTTPS) o 80 (HTTP)
            port = url.getPort() != -1 ? url.getPort() : (url.getProtocol().equals("http") ? 80 : 443);

            if (isHostReachable(host, port, timeout)) {
                return HealthCheckResponse.up("DragonBall API Network Reachable");
            } else {
                return HealthCheckResponse.down("DragonBall API Network Unreachable");
            }
        } catch (Exception e) {
            return HealthCheckResponse.down("DragonBall API Check Error: " + e.getMessage());
        }
    }

    // MÉTODO CLAVE: Abre un socket TCP, si conecta, devuelve true.
    private boolean isHostReachable(String host, int port, int timeout) {
        // El try-with-resources asegura que el socket se cierre inmediatamente
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            // Si cae aquí es porque no hubo conexión (timeout, rechazada, sin internet)
            System.out.println("⚠️ Fallo de conexión TCP a " + host + ": " + e.getMessage());
            return false;
        }
    }
}