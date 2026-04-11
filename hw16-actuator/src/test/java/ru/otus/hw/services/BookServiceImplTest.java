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
import ru.otus.hw.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void findById_shouldReturnBookWithGenresAndAuthor() {
        assertThatCode(() -> {
            BookDto book = bookService.findById(1L);

            assertThat(book.id()).isEqualTo(1L);
            assertThat(book.title()).isEqualTo("BookTitle_1");
            assertThat(book.author()).isNotNull();
            assertThat(book.author().id()).isEqualTo(1L);
            assertThat(book.author().fullName()).isEqualTo("Author_1");
            assertThat(book.genres()).isNotEmpty();
            assertThat(book.genres()).hasSize(2);
        }).doesNotThrowAnyException();
    }

    @Test
    void findById_shouldThrowForNonExistentId() {
        try {
            bookService.findById(999L);
        } catch (EntityNotFoundException e) {
            assertEquals("Book with id 999 not found", e.getMessage());
        }
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
            BookDto newBook = bookService.insert("New book", 1, Set.of(1L, 2L));

            BookDto bookFromService = bookService.findById(newBook.id());
            assertThat(bookFromService.title()).isEqualTo("New book");
            assertThat(bookFromService.author().id()).isEqualTo(1L);
            assertThat(bookFromService.genres().stream().map(GenreDto::id))
                    .containsExactlyInAnyOrder(1L, 2L);
        }).doesNotThrowAnyException();
    }

    @Test
    void update() {
        assertThatCode(() -> {
            bookService.update(1L, "Updated book", 3, Set.of(5L, 6L));

            BookDto updatedBook = bookService.findById(1L);
            assertThat(updatedBook.title()).isEqualTo("Updated book");
            assertThat(updatedBook.author().id()).isEqualTo(3L);
            assertThat(updatedBook.genres().stream().map(GenreDto::id))
                    .containsExactlyInAnyOrder(5L, 6L);
        }).doesNotThrowAnyException();
    }

    @Test
    void delete() {
        assertThatCode(() -> bookService.deleteById(1L)).doesNotThrowAnyException();
        try {
            bookService.findById(1L);
        } catch (EntityNotFoundException e) {
            assertEquals("Book with id 1 not found", e.getMessage());
        }
    }
}
