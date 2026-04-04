package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.models.Book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@DisplayName("Репозиторий на основе ReactiveCrudRepository для работы с книгами ")
@DataR2dbcTest
@Import(BookRepositoryCustomImpl.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReactiveBookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private R2dbcEntityTemplate template;

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        var bookId = 1L;
        Book book = template.select(Book.class)
                .matching(query(where("id").is(bookId)))
                .one()
                .block();
        assertThat(book).isNotNull();

        bookRepository.deleteById(bookId).block();

        book = template.select(Book.class)
                .matching(query(where("id").is(bookId)))
                .one()
                .block();
        assertThat(book).isNull();
    }
}