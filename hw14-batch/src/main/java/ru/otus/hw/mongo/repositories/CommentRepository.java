package ru.otus.hw.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.mongo.models.Comment;

import java.util.Collection;

public interface CommentRepository extends MongoRepository<Comment, String> {
    Collection<Comment> findAllByBookId(String bookId);

    void deleteAllByBookId(String bookId);
}
