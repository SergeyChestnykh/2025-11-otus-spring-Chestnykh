package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jdbc для работы с книгами ")
@DataJpaTest
@Import({JpaBookRepository.class, JpaGenreRepository.class})
class JpaBookRepositoryTest {

    @Autowired
    private JpaBookRepository jpaBookRepository;

    @Autowired
    private TestEntityManager em;

    private List<Author> dbAuthors;

    private List<Genre> dbGenres;

    private List<Book> dbBooks;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
    }

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCorrectBookById(Book expectedBook) {

        var actualBook = jpaBookRepository.findById(expectedBook.getId());

        assertThat(actualBook).isPresent();
        assertThat(actualBook.get().getId()).isEqualTo(expectedBook.getId());
        assertThat(actualBook.get().getTitle()).isEqualTo(expectedBook.getTitle());
        assertThat(actualBook.get().getAuthor()).isEqualTo(expectedBook.getAuthor());
        assertThat(actualBook.get().getGenres())
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(expectedBook.getGenres());
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = jpaBookRepository.findAll();
        var expectedBooks = dbBooks;

        actualBooks.forEach(em::detach);

        assertThat(actualBooks)
                .usingRecursiveComparison()
                .ignoringFields("id", "genres")
                .isEqualTo(expectedBooks);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var expectedBook = new Book(0, "BookTitle_10500", dbAuthors.get(0),
                List.of(dbGenres.get(0), dbGenres.get(2)));

        var returnedBook = jpaBookRepository.save(expectedBook);
        em.detach(returnedBook);

        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .ignoringFields("id")
                .isEqualTo(expectedBook);

        assertThat(em.find(Book.class, expectedBook.getId()))
                .isEqualTo(expectedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expectedBook = new Book(1L, "BookTitle_10500", dbAuthors.get(2),
                List.of(dbGenres.get(4), dbGenres.get(5))
        );

        Book actual = em.find(Book.class, expectedBook.getId());
        assertThat(actual).isNotNull();
        assertThat(actual).isNotEqualTo(expectedBook);
        em.detach(actual);

        var returnedBook = jpaBookRepository.save(expectedBook);
        // почему если раскомментировать то выводит ошибку
        //Expected :Book(id=1, title=BookTitle_10500)
        //Actual   :Book(id=1, title=BookTitle_1)?
//        em.detach(returnedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .ignoringFields("id")
                .ignoringFields("comments")
                .isEqualTo(expectedBook);

        Book book = em.find(Book.class, expectedBook.getId());
        em.detach(book);
        // не смог разобраться почему в book загружаются комментарии и тест проходит - прошу объяснить
        assertThat(book).isEqualTo(expectedBook);
        assertThat(book).isNotEqualTo(actual);

        System.out.println("actual:" + actual);
        System.out.println("expected:" + expectedBook);
        System.out.println("book:" + book);
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        var bookId = 1L;
        assertThat(em.find(Book.class, bookId)).isNotNull();

        jpaBookRepository.deleteById(bookId);

        assertThat(em.find(Book.class, bookId)).isNull();
    }

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }

    private static List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Book(id,
                        "BookTitle_" + id,
                        dbAuthors.get(id - 1),
                        dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2)
                ))
                .toList();
    }

    private static List<Book> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }
}