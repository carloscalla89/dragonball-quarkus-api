package com.pe.demo.quarkus.domain;

import java.util.List;

public interface CharacterRepository {

    Character searchById(Long id);
    List<Character> getAll(int page, int limit);
    void save(Character character);
    boolean delete(String name);
}
