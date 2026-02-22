package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CommentRestController {

    private final CommentService commentService;

    @GetMapping("/api/book/{id}/comments")
    public List<CommentDto> getAllForBook(@PathVariable Long id) {
        return commentService.findAllForBook(id);
    }

    @DeleteMapping("/api/comments/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteById(commentId);
    }

}
