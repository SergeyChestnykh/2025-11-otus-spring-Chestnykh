package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class GenreServiceImplTest {

    @Autowired
    private GenreService genreService;

    @MockitoBean
    private GenreRepository genreRepository;

    @Test
    void findAll_shouldRetry() {
        when(genreRepository.findAll())
                .thenThrow(new org.springframework.dao.TransientDataAccessException("temp") {})
                .thenThrow(new org.springframework.dao.TransientDataAccessException("temp") {})
                .thenThrow(new org.springframework.dao.TransientDataAccessException("temp") {})
                .thenReturn(List.of());

        var result = genreService.findAll();
        assertThat(result.isEmpty());

        verify(genreRepository, times(4)).findAll();
    }
}