package com.pe.demo.quarkus.infrastructure.output.persistence.repository;

import com.pe.demo.quarkus.domain.model.Character;
import com.pe.demo.quarkus.domain.model.CharacterRepository;
import com.pe.demo.quarkus.infrastructure.output.persistence.entity.CharacterJpaEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.util.List;

@Named("panache")
@ApplicationScoped
public class CharacterPanacheRepositoryImpl implements CharacterRepository {

    @Inject
    CharacterPanacheRepository characterPanacheRepository;

    @Override
    public Character searchById(Long id) {
        CharacterJpaEntity entidadDb = characterPanacheRepository.findById(id);

        if (entidadDb != null) {
            return Character.builder()
                    .id(entidadDb.getId())
                    .name(entidadDb.getName())
                    .race(entidadDb.getRace())
                    .ki(parsearKi(entidadDb.getKi()+""))
                    .maxKi(entidadDb.getMaxKi())
                    .image(entidadDb.getImage())
                    .affiliation(entidadDb.getAffiliation())
                    .source("database")
                    .build();
        }

        return null;
    }

    @Override
    @Transactional
    public void save(Character character) {
        CharacterJpaEntity entity = CharacterJpaEntity
                .builder()
                .id(character.getId())
                .name(character.getName())
                .race(character.getRace())
                .ki(character.getKi())
                .maxKi(character.getMaxKi())
                .image(character.getImage())
                .affiliation(character.getAffiliation())
                .build();

        characterPanacheRepository.persist(entity);

    }

    @Transactional
    @Override
    public boolean delete(String nombre) {

        long eliminados = characterPanacheRepository.delete("name",nombre);

        return eliminados > 0;
    }

    @Override
    public List<Character> getAll(int page, int limit) {
        return null;
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
