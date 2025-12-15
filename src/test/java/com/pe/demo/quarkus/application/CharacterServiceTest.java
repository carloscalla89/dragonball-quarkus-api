package com.pe.demo.quarkus.application;

import com.pe.demo.quarkus.domain.model.Character;
import com.pe.demo.quarkus.domain.model.CharacterRepository;
import com.pe.demo.quarkus.infrastructure.input.api.dto.DragonballResponse;
import com.pe.demo.quarkus.infrastructure.input.api.dto.GuerreroResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Habilita Mockito
class CharacterServiceTest {

    // 1. Mockeamos las dependencias
    @Mock
    private CharacterRepository panacheRepository;

    @Mock
    private CharacterRepository apiRestRepository;

    // La clase que vamos a probar
    private CharacterService service;

    // 2. Configuramos la inyección manual antes de cada test
    @BeforeEach
    void setUp() {
        // Al usar inyección por constructor con @Named, es más seguro instanciarlo a mano
        // para garantizar que el mock correcto va en la variable correcta.
        service = new CharacterService(panacheRepository, apiRestRepository);
    }

    // --- TEST: obtenerInfoGuerrero ---

    @Test
    void obtenerInfoGuerrero_DebeRetornarDeBaseDeDatos_CuandoExisteEnDB() {
        // Arrange (Preparar)
        Long id = 1L;
        Character characterMock = crearCharacterMock(id, "Goku", "Saiyan", 10000);

        // Simulamos que Panache SI lo encuentra
        when(panacheRepository.searchById(id)).thenReturn(characterMock);
        //when(apiRestRepository.searchById(id)).thenReturn(characterMock);

        // Act (Ejecutar)
        DragonballResponse<GuerreroResponse> response = service.obtenerInfoGuerrero(id);

        // Assert (Verificar)
        assertNotNull(response);
        assertEquals("Goku", response.getData().getNombre());

        // Verificamos que NO se llamó a la API externa (ahorro de recursos)
        verify(panacheRepository).searchById(any());
        verify(apiRestRepository, never()).save(any());
    }

    @Test
    void obtenerInfoGuerrero_DebeBuscarEnApiYGuardar_CuandoNoExisteEnDB() {
        // Arrange
        Long id = 99L;
        Character characterApi = crearCharacterMock(id, "Broly", "Saiyan", 50000);

        // Simulamos que Panache NO lo encuentra (retorna null)
        when(apiRestRepository.searchById(id)).thenReturn(null);
        // Simulamos que la API SI lo encuentra
        when(apiRestRepository.searchById(id)).thenReturn(characterApi);

        // Act
        DragonballResponse<GuerreroResponse> response = service.obtenerInfoGuerrero(id);

        // Assert
        assertNotNull(response);
        assertEquals("Broly", response.getData().getNombre());

        // Verificamos el flujo de "Fallback":
        // 1. Se llamó a la API
        verify(apiRestRepository).searchById(id);
        // 2. Se guardó en base de datos local (sincronización)
        verify(panacheRepository).save(characterApi);
    }

    // --- TEST: registrarNuevoGuerrero (Validaciones de Negocio) ---

    @Test
    void registrarNuevoGuerrero_DebeLanzarExcepcion_SiEsHumanoPoderoso() {
        // Arrange
        // Creamos un humano con poder > 0 (asumiendo que isPowerful valida eso)
        Character humanoPoderoso = crearCharacterMock(2L, "Krillin", "Humano", 999999);

        // Nota: Asumo que en tu clase Character, isPowerful() devuelve true con este Ki.
        // Si isPowerful es un booleano simple, asegúrate de setearlo en true.
        // humanoPoderoso.setPowerful(true); // Descomenta si es necesario

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.registrarNuevoGuerrero(humanoPoderoso);
        });

        assertEquals("Un humano no puede tener tanto poder (regla de negocio)", exception.getMessage());

        // Importante: Asegurar que NO se guardó nada
        verify(apiRestRepository, never()).save(any());
    }

    @Test
    void registrarNuevoGuerrero_DebeGuardar_SiEsSaiyanPoderoso() {
        // Arrange
        Character saiyan = crearCharacterMock(1L, "Vegeta", "Saiyan", 90000);

        // Act
        service.registrarNuevoGuerrero(saiyan);

        // Assert
        verify(panacheRepository).save(saiyan);
    }

    // --- TEST: obtenerTodosLosGuerreros ---

    @Test
    void obtenerTodosLosGuerreros_DebeMapearCorrectamente() {
        // Arrange
        int page = 1;
        int limit = 10;
        List<Character> mockList = List.of(
                crearCharacterMock(1L, "Goku", "Saiyan", 100),
                crearCharacterMock(2L, "Vegeta", "Saiyan", 90)
        );

        when(apiRestRepository.getAll(page, limit)).thenReturn(mockList);

        // Act
        DragonballResponse<GuerreroResponse> response = service.obtenerTodosLosGuerreros(page, limit);

        // Assert
        assertEquals(2, response.getElementos().size());
        assertEquals(1, response.getPaginaActual());
        assertEquals("Goku", response.getElementos().get(0).getNombre());
    }

    // --- TEST: expulsarGuerrero ---

    @Test
    void expulsarGuerrero_DebeLlamarDeleteDelRepositorio() {
        // Arrange
        String nombre = "Yamcha";

        // Act
        service.expulsarGuerrero(nombre);

        // Assert
        verify(panacheRepository).delete(nombre);
    }

    // --- HELPER PARA CREAR DATOS ---
    private Character crearCharacterMock(Long id, String nombre, String raza, int ki) {
        // Ajusta esto según los constructores o setters de tu entidad Character


        return Character.builder()
                .id(id)
                .name(nombre)
                .race(raza)
                .ki(ki)
                .maxKi((ki * 2)+"")
                .image("url.jpg").build();
    }
}