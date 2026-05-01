package ru.otus.hw.services;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    private final GenreConverter genreConverter;

    @Override
    @Retry(name = "dbRetry")
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream().map(genreConverter::genreToDto).collect(Collectors.toList());
    }
}
