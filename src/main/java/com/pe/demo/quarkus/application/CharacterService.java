package com.pe.demo.quarkus.application;

import com.pe.demo.quarkus.domain.Character;
import com.pe.demo.quarkus.domain.CharacterRepository;
import com.pe.demo.quarkus.infrastructure.api.dto.DragonballResponse;
import com.pe.demo.quarkus.infrastructure.api.dto.GuerreroResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class CharacterService {

    private final CharacterRepository panacheRepository;
    private final CharacterRepository apiRestRepository;

    // Inyección por constructor (Buenas prácticas)
    public CharacterService(@Named("panache")CharacterRepository panacheRepository,
                            @Named("apiRest")CharacterRepository apiRestRepository) {
        this.panacheRepository = panacheRepository;
        this.apiRestRepository = apiRestRepository;
    }

    public DragonballResponse<GuerreroResponse> obtenerInfoGuerrero(Long id) {

        Character character = Optional
                .ofNullable(panacheRepository.searchById(id))
                .orElse(processWhenCharacterIsEmpty(id));

        GuerreroResponse dto = GuerreroResponse.builder()
                .id(character.getId())
                .nombre(character.getName())
                .descripcion(character.getName() + " es un " + character.getRace() + " muy fuerte.")
                .ki(character.getKi()+"")
                .kiMax(character.getMaxKi())
                .imagen(character.getImage())
                .build();

        DragonballResponse<GuerreroResponse> response = new DragonballResponse<>();
        response.setOrigen(dto.getOrigen());
        response.setData(dto);

        return response;

    }

    public DragonballResponse<GuerreroResponse> obtenerTodosLosGuerreros(int page, int limit) {
        List<GuerreroResponse> listaDto = apiRestRepository
                .getAll(page, limit)
                .stream()
                .map(character -> GuerreroResponse.builder()
                        .id(character.getId())
                        .nombre(character.getName())
                        .descripcion(character.getName() + " es un " + character.getRace() + " muy fuerte.")
                        .ki(character.getKi()+"")
                        .kiMax(character.getMaxKi())
                        .imagen(character.getImage())
                        .build()).collect(Collectors.toList());

        DragonballResponse<GuerreroResponse> responseWrapper = new DragonballResponse<>();
        responseWrapper.setElementos(listaDto);
        responseWrapper.setPaginaActual(page);
        responseWrapper.setTotalElementosPorPagina(limit);
        responseWrapper.setTotalElementos(listaDto.size());
        responseWrapper.setOrigen("database");

        return responseWrapper;
    }

    public void registrarNuevoGuerrero(Character character) {
        if ("Humano".equals(character.getRace()) && character.isPowerful()) {
            throw new IllegalArgumentException("Un humano no puede tener tanto poder (regla de negocio)");
        }
        panacheRepository.save(character);
    }

    public void expulsarGuerrero(String nombre) {
        panacheRepository.delete(nombre);
    }

    private Character processWhenCharacterIsEmpty(Long id) {

        Character character = apiRestRepository.searchById(id);
        registrarNuevoGuerrero(character);

        return character;

    }
}
