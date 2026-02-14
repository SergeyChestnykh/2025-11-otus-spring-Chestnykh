package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Репозиторий авторов ")
@DataMongoTest
class AuthorRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(Author.class);
        var dbAuthors = getDbAuthors();
        for (var author : dbAuthors) {
            mongoTemplate.save(author);
        }
    }

    @DisplayName("должен вернуть всех авторов")
    @Test
    void findAll() {
        List<Author> resultAuthors = authorRepository.findAll();

        assertEquals(getDbAuthors(), resultAuthors);
    }

    @DisplayName(" должен вернуть автора по id")
    @Test
    void findById() {
        Author expectedAuthor = getDbAuthors().get(2);

        Optional<Author> resultAuthor = authorRepository.findById(expectedAuthor.getId());

        assertTrue(resultAuthor.isPresent());
        assertEquals(expectedAuthor, resultAuthor.get());
    }

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(String::valueOf)
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }
}