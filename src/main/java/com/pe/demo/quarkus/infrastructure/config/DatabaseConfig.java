package com.pe.demo.quarkus.infrastructure.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "quarkus.datasource")
public interface DatabaseConfig {

    String dbKind();

    String userName();

    String password();

    Jdbc jdbc();

    // Definimos la sub-interfaz para lo que va después de ".jdbc"
    interface Jdbc {

        String url();
    }
}
