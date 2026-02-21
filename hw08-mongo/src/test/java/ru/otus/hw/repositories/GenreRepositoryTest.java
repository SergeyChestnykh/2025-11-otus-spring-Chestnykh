package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Репозиторий жанров ")
@DataMongoTest
class GenreRepositoryTest {
    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MongoTemplate mongoTemplate;


    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(Genre.class);
        var dbGenres = getDbGenres();
        for (var genre : dbGenres) {
            mongoTemplate.save(genre);
        }
    }

    @DisplayName("должен вернуть все жанры")
    @Test
    void findAll() {
        List<Genre> allGenres = genreRepository.findAll();

        assertEquals(getDbGenres(), allGenres);
    }

    @DisplayName("должен вернуть жанры по ids")
    @Test
    void findAllByIds() {
        Genre expectedGenre1 = getDbGenres().get(1);
        Genre expectedGenre2 = getDbGenres().get(2);
        List<Genre> expectedGenres = List.of(expectedGenre1, expectedGenre2);

        Set<String> expectedGenreIds = expectedGenres.stream().map(Genre::getId).collect(Collectors.toSet());
        List<Genre> resultGenres = genreRepository.findAllById(expectedGenreIds);

        assertEquals(expectedGenres, resultGenres);
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(String::valueOf)
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }
}