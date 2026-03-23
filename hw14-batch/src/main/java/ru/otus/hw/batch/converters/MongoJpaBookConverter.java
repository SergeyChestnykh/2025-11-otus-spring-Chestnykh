package ru.otus.hw.batch.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.jpa.models.Author;
import ru.otus.hw.jpa.models.Book;
import ru.otus.hw.jpa.models.Genre;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class MongoJpaBookConverter {

    private final Map<String, Author> authorRelationsHolder;

    private final Map<String, Genre> genreRelationsHolder;

    public Book toBookJpa(ru.otus.hw.mongo.models.Book mongoBook) {
        Book book = new Book();
        List<Genre> genres = mongoBook.getGenres()
                .stream()
                .map(mongoGenre -> genreRelationsHolder.get(mongoGenre.getId()))
                .toList();
        book.setAuthor(authorRelationsHolder.get(mongoBook.getAuthor().getId()));
        book.setGenres(genres);
        book.setTitle(mongoBook.getTitle());
        return book;
    }
}
