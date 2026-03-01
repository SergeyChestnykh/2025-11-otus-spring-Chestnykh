package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;

@Component
public class GenreConverter {
    public String genreToString(GenreDto genreDto) {
        return "Id: %s, Name: %s".formatted(genreDto.id(), genreDto.name());
    }

    public GenreDto genreToDto(Genre genre) {
        return new GenreDto(
                genre.getId(),
                genre.getName()
        );
    }

    public Genre dtoToGenre(GenreDto genreDto) {
        Genre genre = new Genre();
        genre.setId(genreDto.id());
        genre.setName(genreDto.name());
        return genre;
    }
}
