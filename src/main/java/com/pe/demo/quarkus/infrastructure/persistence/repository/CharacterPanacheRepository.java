package com.pe.demo.quarkus.infrastructure.persistence.repository;

import com.pe.demo.quarkus.infrastructure.persistence.entity.CharacterJpaEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CharacterPanacheRepository implements PanacheRepository<CharacterJpaEntity> {


}
