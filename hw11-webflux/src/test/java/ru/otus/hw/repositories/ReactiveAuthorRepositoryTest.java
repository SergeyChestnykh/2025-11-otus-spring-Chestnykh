package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Репозиторий авторов ")
@DataR2dbcTest
class ReactiveAuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @DisplayName("должен вернуть всех авторов")
    @Test
    void findAll() {
        var resultAuthors = authorRepository.findAll()
                .collectList()
                .block();

        assertEquals(getDbAuthors(), resultAuthors);
    }

    @DisplayName("должен вернуть автора по id")
    @Test
    void findById() {
        Author expectedAuthor = getDbAuthors().get(2);

        var resultAuthorMono = authorRepository.findById(expectedAuthor.getId());
        var resultAuthor = resultAuthorMono.block();

        assertEquals(expectedAuthor, resultAuthor);
    }

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }
}
