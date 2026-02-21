package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Репозиторий жанров ")
@DataJpaTest
class JpaGenreRepositoryTest {
    @Autowired
    private GenreRepository genreRepository;

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

        Set<Long> expectedGenreIds = expectedGenres.stream().map(Genre::getId).collect(Collectors.toSet());
        List<Genre> resultGenres = genreRepository.findAllByIdIn(expectedGenreIds);

        assertEquals(expectedGenres, resultGenres);
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }
}