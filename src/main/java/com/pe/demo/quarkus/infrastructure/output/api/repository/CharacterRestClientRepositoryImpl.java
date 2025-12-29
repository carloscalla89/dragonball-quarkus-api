package com.pe.demo.quarkus.infrastructure.output.api.repository;

import com.pe.demo.quarkus.infrastructure.filter.OutgoingRequestFilter;
import com.pe.demo.quarkus.infrastructure.filter.OutgoingResponseFilter;
import com.pe.demo.quarkus.domain.model.Character;
import com.pe.demo.quarkus.domain.model.CharacterRepository;
import com.pe.demo.quarkus.infrastructure.output.api.dto.DragonBallCharacterResponse;
import com.pe.demo.quarkus.infrastructure.output.api.dto.DragonBallPageResponse;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Named("apiRest")
@ApplicationScoped
public class CharacterRestClientRepositoryImpl implements CharacterRepository {

    // 1. Inyectamos la URL base (String) en lugar del cliente
    @ConfigProperty(name = "dragonball.api.url")
    String apiUrl;

    private DragonBallRestClientDefinition dragonBallRestClientDefinition;

    // 2. Construimos el cliente al iniciar el bean
    @PostConstruct
    public void init() {
        this.dragonBallRestClientDefinition =
                RestClientBuilder
                        .newBuilder()
                        .baseUri(URI.create(apiUrl))
                        .register(OutgoingRequestFilter.class)
                        .register(OutgoingResponseFilter.class)
                        .connectTimeout(2, TimeUnit.SECONDS) // Configuración programática
                        .readTimeout(2, TimeUnit.SECONDS)
                .build(DragonBallRestClientDefinition.class);
    }

    @Override
    public Character searchById(Long id) {
        // 3. Usamos el cliente construido manualmente
        DragonBallCharacterResponse dto = dragonBallRestClientDefinition
                .getCharacterById(id);

        return Character.builder()
                .id(dto.getId())
                .name(dto.getName())
                .race(dto.getRace())
                .ki(parsearKi(dto.getKi()))
                .maxKi(dto.getMaxKi())
                .image(dto.getImage())
                .affiliation(dto.getAffiliation())
                .source("api-rest")
                .build();
    }

    @Override
    public List<Character> getAll(int page, int limit) {

        DragonBallPageResponse response = dragonBallRestClientDefinition.getAll(page,limit);

        if (response == null || response.getItems() == null) {
            return List.of();
        }

        return response
                .getItems()
                .stream()
                .map(dto -> Character.builder()
                        .id(dto.getId())
                        .name(dto.getName())
                        .race(dto.getRace())
                        .ki(parsearKi(dto.getKi()))
                        .maxKi(dto.getMaxKi())
                        .image(dto.getImage())
                        .build())
                .collect(Collectors.toList());

    }

    @Override public void save(Character character) {  }
    @Override public boolean delete(String nombre) { return false; }

    private int parsearKi(String kiRaw) {
        try {
            return Integer.parseInt(
                    kiRaw.replace(".", "")
                            .replace(" ", "")
                            .replace("unknown", "0")
            );
        } catch (Exception e) { return 0; }
    }
}
