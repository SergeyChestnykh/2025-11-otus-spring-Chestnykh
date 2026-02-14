package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Book;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookConverter {
    private final AuthorConverter authorConverter;

    private final GenreConverter genreConverter;

    public String bookToString(BookDto bookDto) {
        var genresString = bookDto.genres().stream()
                .map(genreConverter::genreToString)
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));
        return "Id: %d, title: %s, author: {%s}, genres: [%s]".formatted(
                bookDto.id(),
                bookDto.title(),
                bookDto.author(),
                genresString);
    }

    public BookDto bookToDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                authorConverter.authorToDto(book.getAuthor()),
                book.getGenres().stream().map(genreConverter::genreToDto).collect(Collectors.toList()));
    }

    @SuppressWarnings("unused")
    public Book dtoToBook(BookDto bookDto) {
        Book book = new Book();

        if (bookDto.id() != 0) {
            book.setId(bookDto.id());
        }

        book.setTitle(bookDto.title());
        book.setAuthor(authorConverter.dtoToAuthor(bookDto.author()));

        book.setGenres(
                bookDto.genres().stream()
                        .map(genreConverter::dtoToGenre)
                        .collect(Collectors.toList())
        );

        return book;
    }
}
