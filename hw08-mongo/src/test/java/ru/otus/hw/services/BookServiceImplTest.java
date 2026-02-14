package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Import({
        BookServiceImpl.class,
        CommentConverter.class,
        BookConverter.class,
        GenreConverter.class,
        AuthorConverter.class,
})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DataMongoTest
class BookServiceImplTest {

    @Autowired
    private BookServiceImpl bookService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(Book.class);
        mongoTemplate.dropCollection(Author.class);
        mongoTemplate.dropCollection(Genre.class);

        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        var dbBooks = getDbBooks(dbAuthors, dbGenres);

        for (var author : dbAuthors) {
            mongoTemplate.save(author);
        }
        for (var genre : dbGenres) {
            mongoTemplate.save(genre);
        }
        for (var book : dbBooks) {
            mongoTemplate.save(book);
        }
    }

    @Test
    void findById_shouldReturnBookWithGenresAndAuthor() {
        assertThatCode(() -> {
            BookDto book = bookService.findById("1").orElseThrow();

            assertThat(book.id()).isEqualTo("1");
            assertThat(book.title()).isEqualTo("BookTitle_1");
            assertThat(book.author()).isNotNull();
            assertThat(book.author().id()).isEqualTo("1");
            assertThat(book.author().fullName()).isEqualTo("Author_1");
            assertThat(book.genres()).isNotEmpty();
            assertThat(book.genres()).hasSize(2);
        }).doesNotThrowAnyException();
    }

    @Test
    void findById_shouldReturnEmptyForNonExistentId() {
        assertThatCode(() -> {
            Optional<BookDto> book = bookService.findById("999");
            assertThat(book).isEmpty();
        }).doesNotThrowAnyException();
    }

    @Test
    void findAll() {
        assertThatCode(() -> {
            List<BookDto> all = bookService.findAll();
            all.forEach(book -> book.author().fullName());
        }).doesNotThrowAnyException();
    }

    @Test
    void findAllDto() {
        assertThatCode(() -> {
            List<BookDto> all = bookService.findAll();
            all.forEach(book -> book.genres().size());
            all.forEach(book -> book.author().fullName());
        }).doesNotThrowAnyException();
    }

    @Test
    void insert() {
        assertThatCode(() -> {
            BookDto newBook = bookService.insert("New book", "1", Set.of("1", "2"));

            Optional<BookDto> bookFromServiceOpt = bookService.findById(newBook.id());
            BookDto bookFromService = bookFromServiceOpt.orElseThrow();
            assertThat(bookFromService.title()).isEqualTo("New book");
            assertThat(bookFromService.author().id()).isEqualTo("1");
            assertThat(bookFromService.genres().stream().map(GenreDto::id))
                    .containsExactlyInAnyOrder("1", "2");
        }).doesNotThrowAnyException();
    }

    @Test
    void update() {
        assertThatCode(() -> {
            bookService.update("1", "Updated book", "3", Set.of("5", "6"));

            BookDto updatedBook = bookService.findById("1").orElseThrow();
            assertThat(updatedBook.title()).isEqualTo("Updated book");
            assertThat(updatedBook.author().id()).isEqualTo("3");
            assertThat(updatedBook.genres().stream().map(GenreDto::id))
                    .containsExactlyInAnyOrder("5", "6");
        }).doesNotThrowAnyException();
    }

    @Test
    void delete() {
        assertThatCode(() -> {
            bookService.deleteById("1");

            assertThat(bookService.findById("1")).isEmpty();
        }).doesNotThrowAnyException();
    }

    @Test
    void updateAuthor_shouldReflectInBook() {
        Author authorToUpdate = mongoTemplate.findById("1", Author.class);
        String newAuthorFullName = "Updated_Author_1";
        authorToUpdate.setFullName(newAuthorFullName);
        mongoTemplate.save(authorToUpdate);

        BookDto bookAfter = bookService.findById("1").orElseThrow();

        String actualAuthorFullName = bookAfter.author().fullName();
        assertEquals(newAuthorFullName, actualAuthorFullName,
                "Автор в книге должен быть обновлен после изменения в коллекции authors");
    }

    @Test
    void updateGenre_shouldReflectInBook() {
        Genre genreToUpdate = mongoTemplate.findById("1", Genre.class);
        String newGenreName = "Updated_Genre_1";
        genreToUpdate.setName(newGenreName);
        mongoTemplate.save(genreToUpdate);

        BookDto bookAfter = bookService.findById("1").orElseThrow();

        String actualGenreName = bookAfter.genres().get(0).name();
        assertEquals(newGenreName, actualGenreName,
                "Жанр в книге должен быть обновлен после изменения в коллекции genres");
    }


    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(String::valueOf)
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(String::valueOf)
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }

    private static List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Book(id.toString(),
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
