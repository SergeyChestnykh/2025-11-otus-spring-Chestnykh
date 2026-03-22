package ru.otus.hw.jpa.repositories;


import ru.otus.hw.jpa.models.Genre;

import java.util.List;
import java.util.Set;

public interface GenreRepository {
    List<Genre> findAll();

    List<Genre> findAllByIds(Set<Long> ids);
}
