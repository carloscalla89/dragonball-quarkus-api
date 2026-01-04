package com.pe.demo.quarkus.infrastructure.metric;

import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@ApplicationScoped
public class AppLifecycleBean {

    private final MeterRegistry registry;
    private final AtomicReference<Double> startupTimeRef = new AtomicReference<>(Double.NaN);

    public AppLifecycleBean(MeterRegistry registry) {
        this.registry = registry;
        registry.gauge("app_startup_time_seconds",
                startupTimeRef,
                AtomicReference::get);
    }

    void onStart(@Observes StartupEvent ev) {
        // En Quarkus Nativo, getUptime() devuelve el tiempo desde que el proceso arrancó
        // hasta este momento preciso (StartupEvent).
        long startupTimeInMillis = ManagementFactory.getRuntimeMXBean().getUptime();
        double startupSeconds = startupTimeInMillis / 1000.0;

        startupTimeRef.set(startupSeconds);

        String msg = "🚀 App iniciada en " + (startupTimeInMillis / 1000.0) + "s. Métrica registrada.";
        log.info(msg);
    }
}
