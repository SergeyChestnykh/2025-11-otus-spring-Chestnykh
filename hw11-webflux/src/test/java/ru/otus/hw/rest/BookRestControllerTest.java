package ru.otus.hw.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.BookService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest({BookRestController.class, BookConverter.class, AuthorConverter.class, GenreConverter.class})
class BookRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BookConverter bookConverter;

    @MockitoBean
    private BookService bookService;

    @Test
    void getAllBooks_ReturnsBooksList() {
        AuthorDto author = new AuthorDto(1L, "Author Name");
        GenreDto genre = new GenreDto(1L, "Genre Name");
        BookDto book = new BookDto(1L, "Book Title", author, List.of(genre));

        when(bookService.findAll()).thenReturn(Flux.fromIterable(List.of(book)));

        webTestClient.get().uri("/api/book")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDto.class)
                .contains(book);

        verify(bookService).findAll();
    }

    @Test
    void getAllBooks_ReturnsEmptyList() {
        when(bookService.findAll()).thenReturn(Flux.empty());

        webTestClient.get().uri("/api/book")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDto.class)
                .isEqualTo(Collections.emptyList());

        verify(bookService).findAll();
    }

    @Test
    void getBookById_ReturnsBook() {
        AuthorDto author = new AuthorDto(1L, "Author Name");
        GenreDto genre = new GenreDto(1L, "Genre Name");
        BookDto book = new BookDto(1L, "Book Title", author, List.of(genre));

        when(bookService.findById(1L)).thenReturn(Mono.just(book));

        webTestClient.get().uri("/api/book/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .isEqualTo(book);

        verify(bookService).findById(1L);
    }

    @Test
    void deleteBook_DeletesSuccessfully() {
        webTestClient.delete().uri("/api/book/1")
                .exchange()
                .expectStatus().isOk();

        verify(bookService).deleteById(1L);
    }

    @Test
    void createBook_CreatesSuccessfully() {
        AuthorDto author = new AuthorDto(1L, "Author Name");
        GenreDto genre = new GenreDto(1L, "Genre Name");
        BookDto createdBook = new BookDto(1L, "New Book", author, List.of(genre));
        BookFormDto formDto = bookConverter.toFormDto(createdBook);

        when(bookService.insert(eq("New Book"), eq(1L), any()))
                .thenReturn(Mono.just(createdBook));

        webTestClient.post().uri("/api/book")
                .bodyValue(formDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BookDto.class)
                .isEqualTo(createdBook);

        verify(bookService).insert(eq("New Book"), eq(1L), any());
    }

    @Test
    void updateBook_UpdatesSuccessfully() {
        AuthorDto author = new AuthorDto(2L, "Updated Author");
        GenreDto genre = new GenreDto(1L, "Genre Name");
        BookDto updatedBook = new BookDto(1L, "Updated Book", author, List.of(genre));
        BookFormDto formDto = bookConverter.toFormDto(updatedBook);

        when(bookService.update(eq(1L), eq("Updated Book"), eq(2L), any()))
                .thenReturn(Mono.just(updatedBook));

        webTestClient.put().uri("/api/book/1")
                .bodyValue(formDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .isEqualTo(updatedBook);

        verify(bookService).update(eq(1L), eq("Updated Book"), eq(2L), any());
    }
}
