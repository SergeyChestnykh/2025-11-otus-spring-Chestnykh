package ru.otus.hw.repositories;

import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Optional<Comment> add(long bookId, String text);

    void update(long commentId, String text);

    Optional<Comment> findById(long id);

    List<Comment> findAllForBook(long bookId);

    void deleteById(long id);
}
