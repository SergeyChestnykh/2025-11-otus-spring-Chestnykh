package ru.otus.hw.repositories;

import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Comment save(Comment comment);

    void update(long commentId, String text);

    Optional<Comment> findById(long id);

    List<Comment> findAllForBook(long bookId);

    void deleteById(long id);
}
