package ru.otus.hw.jpa.repositories;

import ru.otus.hw.jpa.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Comment save(Comment comment);

    Optional<Comment> findById(long id);

    List<Comment> findAllForBook(long bookId);

    void deleteById(long id);
}
