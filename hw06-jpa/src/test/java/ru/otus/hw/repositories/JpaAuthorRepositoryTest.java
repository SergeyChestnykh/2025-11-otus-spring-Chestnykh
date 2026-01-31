package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Репозиторий авторов ")
@DataJpaTest
@Import(JpaAuthorRepository.class)
class JpaAuthorRepositoryTest {

    @Autowired
    private JpaAuthorRepository jpaAuthorRepository;

    @DisplayName("должен вернуть всех авторов")
    @Test
    void findAll() {
        List<Author> resultAuthors = jpaAuthorRepository.findAll();

        assertEquals(getDbAuthors(), resultAuthors);
    }

    @DisplayName(" должен вернуть автора по id")
    @Test
    void findById() {
        Author expectedAuthor = getDbAuthors().get(2);

        Optional<Author> resultAuthor = jpaAuthorRepository.findById(expectedAuthor.getId());

        assertTrue(resultAuthor.isPresent());
        assertEquals(expectedAuthor, resultAuthor.get());
    }

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }
}