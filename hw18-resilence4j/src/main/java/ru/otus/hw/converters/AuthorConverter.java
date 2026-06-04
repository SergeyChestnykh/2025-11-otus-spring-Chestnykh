package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;

@Component
public class AuthorConverter {

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
