package ru.otus.hw.repositories;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;
import static ru.otus.hw.repositories.DbDataProvider.getDbAuthors;
import static ru.otus.hw.repositories.DbDataProvider.getDbGenres;

@DisplayName("BookRepositoryCustomImpl для работы с книгами ")
@DataR2dbcTest
@Import(BookRepositoryCustomImpl.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookRepositoryCustomImplTest {

    @Autowired
    private BookRepositoryCustom bookRepositoryCustom;

    @Autowired
    private R2dbcEntityTemplate template;

    private List<Author> dbAuthors;

    private List<Genre> dbGenres;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
    }


    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = bookRepositoryCustom.findAllWithAuthorAndGenres().collectList().block();
        var expectedBooks = DbDataProvider.getDbBooks();

        assertThat(actualBooks)
                .usingRecursiveComparison()
                .ignoringFields("id", "genres")
                .isEqualTo(expectedBooks);
    }

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCorrectBookById(Book expectedBook) {

        var actualBook = bookRepositoryCustom.findByIdWithAuthorAndGenres(expectedBook.getId()).blockOptional();

        Assertions.assertThat(actualBook).isPresent();
        Assertions.assertThat(actualBook.get().getId()).isEqualTo(expectedBook.getId());
        Assertions.assertThat(actualBook.get().getTitle()).isEqualTo(expectedBook.getTitle());
        Assertions.assertThat(actualBook.get().getAuthor()).isEqualTo(expectedBook.getAuthor());
        Assertions.assertThat(actualBook.get().getGenres())
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(expectedBook.getGenres());
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var expectedBook = new Book(0, "BookTitle_10500", dbAuthors.get(0),
                List.of(dbGenres.get(0), dbGenres.get(2)));

        var returnedBook = bookRepositoryCustom.save(expectedBook).block();

        Assertions.assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .ignoringFields("id")
                .isEqualTo(expectedBook);

        Book savedBook = template
                .select(Book.class)
                .matching(query(where("id").is(expectedBook.getId())))
                .one()
                .block();
        Assertions.assertThat(savedBook).isEqualTo(expectedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expectedBook = new Book(1L, "BookTitle_10500", dbAuthors.get(2),
                List.of(dbGenres.get(4), dbGenres.get(5))
        );

//        Book actual = em.find(Book.class, expectedBook.getId());
        Book actual = template.select(Book.class)
                .matching(query(where("id").is(expectedBook.getId())))
                .one()
                .block();
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual).isNotEqualTo(expectedBook);

        var returnedBook = bookRepositoryCustom.save(expectedBook).block();
        Assertions.assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .ignoringFields("id")
                .ignoringFields("comments")
                .isEqualTo(expectedBook);

        Book book = template.select(Book.class)
                .matching(query(where("id").is(expectedBook.getId())))
                .one()
                .block();
        Assertions.assertThat(book).isEqualTo(expectedBook);
        Assertions.assertThat(book).isNotEqualTo(actual);

        System.out.println("actual:" + actual);
        System.out.println("expected:" + expectedBook);
        System.out.println("book:" + book);
    }

    static List<Book> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return DbDataProvider.getDbBooks(dbAuthors, dbGenres);
    }
}