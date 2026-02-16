package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;

@Component
public class AuthorConverter {
    public String authorToString(AuthorDto authorDto) {
        return "Id: %d, FullName: %s".formatted(authorDto.id(), authorDto.fullName());
    }

    public AuthorDto authorToDto(Author author) {
        return new AuthorDto(
                author.getId(),
                author.getFullName()
        );
    }

    public Author dtoToAuthor(AuthorDto authorDto) {
        Author author = new Author();
        if (authorDto.id() != 0) {
            author.setId(authorDto.id());
        }
        author.setFullName(authorDto.fullName());
        return author;
    }
}
