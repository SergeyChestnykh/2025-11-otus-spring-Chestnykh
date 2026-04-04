package ru.otus.hw.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(GenreRestController.class)
class GenreRestControllerTest {

    @MockitoBean
    private GenreService genreService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getAllGenres_ReturnsGenresList() {
        GenreDto genre1 = new GenreDto(1L, "Genre One");
        GenreDto genre2 = new GenreDto(2L, "Genre Two");
        GenreDto genre3 = new GenreDto(3L, "Genre Three");

        when(genreService.findAll()).thenReturn(Flux.fromIterable(List.of(genre1, genre2, genre3)));

        webTestClient.get().uri("/api/genres")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(GenreDto.class)
                .contains(genre1, genre2, genre3);

        verify(genreService).findAll();
    }
}
