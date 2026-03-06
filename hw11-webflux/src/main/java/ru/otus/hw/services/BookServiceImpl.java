package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.BookRepositoryCustom;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookRepositoryCustom bookRepositoryCustom;

    private final BookConverter bookConverter;

    @Override
    @Transactional(readOnly = true)
    public Mono<BookDto> findById(long id) {
        return bookRepositoryCustom.findByIdWithAuthorAndGenres(id).map(bookConverter::bookToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<BookDto> findAll() {
        return bookRepositoryCustom.findAllWithAuthorAndGenres().map(bookConverter::bookToDto);
    }

    @Override
    @Transactional
    public Mono<BookDto> insert(String title, long authorId, Set<Long> genresIds) {
        return save(0, title, authorId, genresIds);
    }

    @Transactional
    @Override
    public Mono<BookDto> update(long id, String title, long authorId, Set<Long> genresIds) {
        return save(id, title, authorId, genresIds);
    }

    @Transactional
    @Override
    public Mono<Void> deleteById(long id) {
        return bookRepository.deleteById(id);
    }

    private Mono<BookDto> save(long id,
                               String title,
                               long authorId,
                               Set<Long> genresIds) {

        if (genresIds == null || genresIds.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Genres ids must not be null or empty"));
        }

        Mono<Author> authorMono = authorRepository.findById(authorId)
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException("Author with id %d not found".formatted(authorId))
                ));

        Mono<List<Genre>> genresMono = genreRepository.findAllByIdIn(genresIds)
                .collectList()
                .flatMap(genres -> {
                    if (genres.size() != genresIds.size()) {
                        return Mono.error(
                                new EntityNotFoundException("One or more genres not found: %s".formatted(genresIds)));
                    }
                    return Mono.just(genres);
                });

        return Mono.zip(authorMono, genresMono)
                .flatMap(tuple -> {
                    Author author = tuple.getT1();
                    List<Genre> genres = tuple.getT2();

                    Book book = new Book(id, title, author, genres);

                    return bookRepositoryCustom.save(book);
                })
                .map(bookConverter::bookToDto);
    }
}
