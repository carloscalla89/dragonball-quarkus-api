package com.pe.demo.quarkus.infrastructure.persistence.repository;

import com.pe.demo.quarkus.domain.Character;
import com.pe.demo.quarkus.domain.CharacterRepository;
import com.pe.demo.quarkus.domain.CharacterResponse;
import com.pe.demo.quarkus.infrastructure.persistence.entity.CharacterJpaEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.util.List;

@Named("panache")
@ApplicationScoped
public class CharacterPanacheRepositoryImpl implements CharacterRepository {

    @Inject
    CharacterPanacheRepository dbRepository;

    @Override
    public Character searchById(Long id) {
        CharacterJpaEntity entidadDb = dbRepository.findById(id);

        if (entidadDb != null) {
            return Character.builder()
                    .id(entidadDb.getId())
                    .name(entidadDb.getName())
                    .race(entidadDb.getRace())
                    .ki(parsearKi(entidadDb.getKi()+""))
                    .source("database")
                    .build();
        }

        return null;
    }

    @Override
    public List<Character> getAll(int page, int limit) {
        return null;
    }

    @Override
    @Transactional
    public void save(Character character) {
        CharacterJpaEntity entity = CharacterJpaEntity
                .builder()
                .name(character.getName())
                .race(character.getRace())
                .ki(character.getKi())
                .maxKi(character.getMaxKi())
                .image(character.getImage())
                .build();

        dbRepository.persist(entity);

    }

    @Transactional
    @Override
    public boolean delete(String nombre) {

        long eliminados = dbRepository.delete("name",nombre);

        return eliminados > 0;
    }

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
