package ru.otus.hw.services;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;

public interface CommentService {
    Flux<CommentDto> findAllForBook(long bookId);

    Mono<CommentDto> find(long id);

    Mono<CommentDto> insert(long bookId, String text);

    Mono<CommentDto> update(long id, String text);

    Mono<Void> deleteById(long id);
}
