package ru.otus.hw.batch.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.batch.cache.MongoIdRelationCache;
import ru.otus.hw.jpa.models.Author;
import ru.otus.hw.jpa.models.Book;
import ru.otus.hw.jpa.models.Genre;

import java.util.List;

@RequiredArgsConstructor
@Component
public class MongoJpaBookConverter {

    private final MongoIdRelationCache<Genre> genreMongoIdRelationCache;

    private final MongoIdRelationCache<Author> authorMongoIdRelationCache;

    public Book toBookJpa(ru.otus.hw.mongo.models.Book mongoBook) {
        Book book = new Book();
        List<Genre> genres = mongoBook.getGenres()
                .stream()
                .map(mongoGenre -> genreMongoIdRelationCache.get(mongoGenre.getId()))
                .toList();
        book.setAuthor(authorMongoIdRelationCache.get(mongoBook.getAuthor().getId()));
        book.setGenres(genres);
        book.setTitle(mongoBook.getTitle());
        return book;
    }
}
