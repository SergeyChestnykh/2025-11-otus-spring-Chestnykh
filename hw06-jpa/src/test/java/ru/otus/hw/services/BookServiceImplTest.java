package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class BookServiceImplTest {

    @Autowired
    private BookService bookService;

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