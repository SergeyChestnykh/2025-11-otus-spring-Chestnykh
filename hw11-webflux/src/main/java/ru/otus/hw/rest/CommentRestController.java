package ru.otus.hw.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

@RequiredArgsConstructor
@RestController
public class CommentRestController {

    private final CommentService commentService;

    @GetMapping("/api/book/{id}/comments")
    public Flux<CommentDto> getAllForBook(@PathVariable Long id) {
        return commentService.findAllForBook(id);
    }

    @DeleteMapping("/api/comments/{commentId}")
    public Mono<Void> deleteComment(@PathVariable Long commentId) {
        return commentService.deleteById(commentId);
    }

    @PostMapping("/api/book/{bookId}/comments")
    public Mono<CommentDto> addComment(@PathVariable Long bookId, @Valid @RequestBody CommentDto commentDto) {
        return commentService.insert(bookId, commentDto.text());
    }

}
