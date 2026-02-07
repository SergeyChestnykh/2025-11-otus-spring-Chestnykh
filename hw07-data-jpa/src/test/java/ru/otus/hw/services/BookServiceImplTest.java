package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@Import({
        BookServiceImpl.class,
        CommentConverter.class,
        BookConverter.class,
        GenreConverter.class,
        AuthorConverter.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class BookServiceImplTest {

    @Autowired
    private BookServiceImpl bookService;

    @Test
    void findById() {
        BookDto book = bookService.findById(1L).orElseThrow();

        assertThatCode(() -> book.genres().size()).doesNotThrowAnyException();
        assertThat(book.genres()).isNotEmpty();
    }

    @Test
    void findAll() {
        List<BookDto> all = bookService.findAll();

        assertThatCode(() -> {
            all.forEach(book -> book.author().fullName());
        }).doesNotThrowAnyException();
    }

    @Test
    void findAllDto() {
        List<ru.otus.hw.dto.BookDto> all = bookService.findAll();

        assertThatCode(() -> {
            all.forEach(book -> {
                book.genres().size();
            });
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            all.forEach(book -> {
                book.author().fullName();
            });
        }).doesNotThrowAnyException();
    }

    @Test
    void insert() {
        ru.otus.hw.dto.BookDto newBook = bookService.insert("New book", 1, Set.of(1L, 2L));

        Optional<BookDto> bookFromServiceOpt = bookService.findById(newBook.id());
        BookDto bookFromService = bookFromServiceOpt.orElseThrow();
        assertThat(bookFromService.title()).isEqualTo("New book");
        assertThat(bookFromService.author().id()).isEqualTo(1L);
        assertThat(bookFromService.genres().stream().map(GenreDto::id))
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void update() {
        bookService.update(1L, "Updated book", 3, Set.of(5L, 6L));

        BookDto updatedBook = bookService.findById(1L).orElseThrow();
        assertThat(updatedBook.title()).isEqualTo("Updated book");
        assertThat(updatedBook.author().id()).isEqualTo(3L);
        assertThat(updatedBook.genres().stream().map(GenreDto::id))
                .containsExactlyInAnyOrder(5L, 6L);
    }

    @Test
    void delete() {
        bookService.deleteById(1L);

        assertThat(bookService.findById(1L)).isEmpty();
    }
}