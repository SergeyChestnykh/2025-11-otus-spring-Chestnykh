package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Book;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookConverter {
    private final AuthorConverter authorConverter;

    private final GenreConverter genreConverter;

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

    public BookFormDto toFormDto(BookDto bookDto) {
        return new BookFormDto(
                bookDto.title(),
                bookDto.author().id(),
                bookDto.genres().stream()
                        .map(GenreDto::id)
                        .toList()
        );
    }
}
