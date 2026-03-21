package ru.otus.hw.repositories;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Book;

public interface BookRepositoryCustom {
    Flux<Book> findAllWithAuthorAndGenres();

    Mono<Book> findByIdWithAuthorAndGenres(long id);

    Mono<Book> save(Book book);
}