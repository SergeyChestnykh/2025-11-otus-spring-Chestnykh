package ru.otus.hw.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.Author;

public interface AuthorRepository extends ReactiveCrudRepository<Author, Long> {
    @NonNull
    Flux<Author> findAll();
}
