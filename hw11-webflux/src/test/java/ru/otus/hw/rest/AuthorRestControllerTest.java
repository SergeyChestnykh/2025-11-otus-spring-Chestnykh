package ru.otus.hw.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(AuthorRestController.class)
class AuthorRestControllerTest {

    @MockitoBean
    private AuthorService authorService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getAllAuthors_ReturnsAuthorsList() {
        AuthorDto author1 = new AuthorDto(1L, "Author One");
        AuthorDto author2 = new AuthorDto(2L, "Author Two");

        when(authorService.findAll()).thenReturn(Flux.fromIterable(List.of(author1, author2)));

        webTestClient.get().uri("/api/authors")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuthorDto.class)
                .contains(author1, author2);

        verify(authorService).findAll();
    }
}
