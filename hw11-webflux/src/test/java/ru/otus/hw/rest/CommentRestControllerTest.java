package ru.otus.hw.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(CommentRestController.class)
class CommentRestControllerTest {

    @MockitoBean
    private CommentService commentService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getAllCommentsForBook_ReturnsCommentsList() {
        CommentDto comment1 = new CommentDto(1L, "Comment One");
        CommentDto comment2 = new CommentDto(2L, "Comment Two");

        when(commentService.findAllForBook(1L)).thenReturn(Flux.fromIterable(List.of(comment1, comment2)));

        webTestClient.get().uri("/api/book/1/comments")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CommentDto.class)
                .contains(comment1, comment2);

        verify(commentService).findAllForBook(1L);
    }

    @Test
    void getAllCommentsForBook_ReturnsEmptyList() {
        when(commentService.findAllForBook(1L)).thenReturn(Flux.empty());

        webTestClient.get().uri("/api/book/1/comments")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CommentDto.class)
                .hasSize(0);

        verify(commentService).findAllForBook(1L);
    }

    @Test
    void deleteComment_DeletesSuccessfully() {
        webTestClient.delete().uri("/api/comments/1")
                .exchange()
                .expectStatus().isOk();

        verify(commentService).deleteById(1L);
    }

    @Test
    void addComment_CreatesSuccessfully() {
        CommentDto commentDto = new CommentDto(1L, "New Comment");

        when(commentService.insert(eq(1L), eq("New Comment"))).thenReturn(Mono.just(commentDto));

        webTestClient.post().uri("/api/book/1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"text\":\"New Comment\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CommentDto.class)
                .isEqualTo(commentDto);

        verify(commentService).insert(eq(1L), eq("New Comment"));
    }

    @Test
    void addComment_WithValidText_CreatesSuccessfully() {
        CommentDto commentDto = new CommentDto(1L, "This is a valid comment");

        when(commentService.insert(eq(1L), eq("This is a valid comment"))).thenReturn(Mono.just(commentDto));

        webTestClient.post().uri("/api/book/1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"text\":\"This is a valid comment\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CommentDto.class)
                .isEqualTo(commentDto);

        verify(commentService).insert(eq(1L), eq("This is a valid comment"));
    }
}
