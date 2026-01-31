package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.repositories.JpaAuthorRepository;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaGenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@Import({
        BookServiceImpl.class,
        JpaAuthorRepository.class,
        JpaBookRepository.class,
        JpaGenreRepository.class
})
class BookServiceImplTest {

    @Autowired
    private BookServiceImpl bookService;

    @Test
    void findById() {
        Book book = bookService.findById(1L).orElseThrow();

        assertThatCode(() -> book.getGenres().size()).doesNotThrowAnyException();
        assertThat(book.getGenres()).isNotEmpty();
    }

    @Test
    void findAll() {
        List<Book> all = bookService.findAll();

        assertThatCode(() -> {
            all.forEach(book -> {
                book.getGenres().size();
            });
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            all.forEach(book -> {
                book.getAuthor().getFullName();
            });
        }).doesNotThrowAnyException();
    }

    @Test
    void insert() {
        Book newBook = bookService.insert("New book", 1, Set.of(1L, 2L));

        Optional<Book> bookFromServiceOpt = bookService.findById(newBook.getId());
        Book bookFromService = bookFromServiceOpt.orElseThrow();
        assertThat(bookFromService.getTitle()).isEqualTo("New book");
        assertThat(bookFromService.getAuthor().getId()).isEqualTo(1L);
        assertThat(bookFromService.getGenres().stream().map(Genre::getId))
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void update() {
        bookService.update(1L, "Updated book", 3, Set.of(5L, 6L));

        Book updatedBook = bookService.findById(1L).orElseThrow();
        assertThat(updatedBook.getTitle()).isEqualTo("Updated book");
        assertThat(updatedBook.getAuthor().getId()).isEqualTo(3L);
        assertThat(updatedBook.getGenres().stream().map(Genre::getId))
                .containsExactlyInAnyOrder(5L, 6L);
    }

    @Test
    void delete() {
        bookService.deleteById(1L);

        assertThat(bookService.findById(1L)).isEmpty();
    }
}