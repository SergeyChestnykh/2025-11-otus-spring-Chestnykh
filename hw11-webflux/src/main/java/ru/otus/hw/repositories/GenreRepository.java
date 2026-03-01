package ru.otus.hw.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.Genre;

import java.util.Set;

public interface GenreRepository extends ReactiveCrudRepository<Genre, Long> {
    @Override
    @NonNull
    Flux<Genre> findAll();

    Flux<Genre> findAllByIdIn(Set<Long> ids);
}
