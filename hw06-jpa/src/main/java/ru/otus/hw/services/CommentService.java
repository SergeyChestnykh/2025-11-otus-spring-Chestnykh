package ru.otus.hw.services;

import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    List<Comment> findAllForBook(long bookId);

    Optional<Comment> find(long commentId);

    Comment insert(long bookId, String text);

    Comment update(long commentId, String text);

    void deleteById(long id);
}
