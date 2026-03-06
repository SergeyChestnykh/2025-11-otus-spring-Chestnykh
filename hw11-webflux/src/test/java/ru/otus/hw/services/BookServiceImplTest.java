package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.BookRepositoryCustom;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DataR2dbcTest
@Import({
        BookServiceImpl.class,
        CommentConverter.class,
        BookConverter.class,
        GenreConverter.class,
        AuthorConverter.class,
})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class BookServiceImplTest {

    @Autowired
    private BookServiceImpl bookService;

    @MockitoBean
    private BookRepositoryCustom bookRepositoryCustom;

    @MockitoBean
    private BookRepository bookRepository;

    @MockitoBean
    private AuthorRepository authorRepository;

    @MockitoBean
    private GenreRepository genreRepository;

    @Autowired
    private BookConverter bookConverter;

    @Test
    void findById_shouldReturnBookWithGenresAndAuthor() {
        Author author = new Author(1L, "Author_1");
        Genre genre1 = new Genre(1L, "Genre_1");
        Genre genre2 = new Genre(2L, "Genre_2");
        Book book = new Book(1L, "BookTitle_1", author, List.of(genre1, genre2));
        BookDto bookDto = bookConverter.bookToDto(book);

        when(bookRepositoryCustom.findByIdWithAuthorAndGenres(1L)).thenReturn(Mono.just(book));

        BookDto result = bookService.findById(1L).block();

        assertThat(result).isEqualTo(bookDto);
        verify(bookRepositoryCustom).findByIdWithAuthorAndGenres(1L);
    }

    @Test
    void findAll() {
        Author author1 = new Author(1L, "Author_1");
        Author author2 = new Author(2L, "Author_2");
        Genre genre1 = new Genre(1L, "Genre_1");
        Genre genre2 = new Genre(2L, "Genre_2");
        Genre genre3 = new Genre(3L, "Genre_3");
        Genre genre4 = new Genre(4L, "Genre_4");

        Book book1 = new Book(1L, "BookTitle_1", author1, List.of(genre1, genre2));
        Book book2 = new Book(2L, "BookTitle_2", author2, List.of(genre3, genre4));

        when(bookRepositoryCustom.findAllWithAuthorAndGenres())
                .thenReturn(Flux.just(book1, book2));

        List<BookDto> result = bookService.findAll().collectList().block();

        assertThat(result).hasSize(2);
        verify(bookRepositoryCustom).findAllWithAuthorAndGenres();
    }

    @Test
    void findAllDto() {
        Author author1 = new Author(1L, "Author_1");
        Author author2 = new Author(2L, "Author_2");
        Genre genre1 = new Genre(1L, "Genre_1");
        Genre genre2 = new Genre(2L, "Genre_2");
        Genre genre3 = new Genre(3L, "Genre_3");
        Genre genre4 = new Genre(4L, "Genre_4");

        Book book1 = new Book(1L, "BookTitle_1", author1, List.of(genre1, genre2));
        Book book2 = new Book(2L, "BookTitle_2", author2, List.of(genre3, genre4));

        when(bookRepositoryCustom.findAllWithAuthorAndGenres())
                .thenReturn(Flux.just(book1, book2));

        List<BookDto> result = bookService.findAll().collectList().block();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).author().fullName()).isEqualTo("Author_1");
        assertThat(result.get(1).author().fullName()).isEqualTo("Author_2");
        verify(bookRepositoryCustom).findAllWithAuthorAndGenres();
    }

    @Test
    void insert() {
        Author author = new Author(3L, "new Author Name");
        Genre genre = new Genre(5L, "new Genre Name");
        Book newBook = new Book(0L, "New book", author, List.of(genre));
        BookDto bookDto = bookConverter.bookToDto(newBook);

        when(authorRepository.findById(3L)).thenReturn(Mono.just(author));
        when(genreRepository.findAllByIdIn(Set.of(5L))).thenReturn(Flux.just(genre));
        when(bookRepositoryCustom.save(any())).thenReturn(Mono.just(newBook));

        BookDto result = bookService.insert(
                        newBook.getTitle(),
                        newBook.getAuthor().getId(),
                        newBook.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()))
                .block();

        assertThat(result).isEqualTo(bookDto);
        verify(authorRepository).findById(newBook.getAuthor().getId());
        verify(genreRepository).findAllByIdIn(
                newBook.getGenres().stream().map(Genre::getId).collect(Collectors.toSet())
        );
        verify(bookRepositoryCustom).save(newBook);
    }

    @Test
    void update() {
        Author author = new Author(3L, "Author Name");
        Genre genre = new Genre(5L, "Genre Name");
        Book book = new Book(1L, "Updated book", author, List.of(genre));
        BookDto bookDto = bookConverter.bookToDto(book);

        when(authorRepository.findById(3L)).thenReturn(Mono.just(author));
        when(genreRepository.findAllByIdIn(Set.of(5L))).thenReturn(Flux.just(genre));
        when(bookRepositoryCustom.save(any())).thenReturn(Mono.just(book));

        BookDto result = bookService.update(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor().getId(),
                        book.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()))
                .block();

        assertThat(result).isEqualTo(bookDto);
        verify(bookRepositoryCustom).save(book);
    }

    @Test
    void delete() {
        when(bookRepository.deleteById(1L)).thenReturn(Mono.empty());

        bookService.deleteById(1L).block();

        verify(bookRepository).deleteById(1L);
    }
}
