package ru.otus.hw.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Book;

public interface BookRepository extends ReactiveCrudRepository<Book, Long> {
    @Override
    @NonNull
    Flux<Book> findAll();

    @Override
    @NonNull
    Mono<Book> findById(@NonNull Long id);
}
