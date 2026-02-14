package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

public interface GenreRepository extends MongoRepository<Genre, Long> {
    @Override
    @NonNull
    List<Genre> findAll();

    List<Genre> findAllByIdIn(Set<Long> ids);
}
