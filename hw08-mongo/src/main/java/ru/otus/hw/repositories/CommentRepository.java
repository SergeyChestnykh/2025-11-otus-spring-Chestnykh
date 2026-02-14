package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.Comment;

import java.util.Collection;

public interface CommentRepository extends MongoRepository<Comment, String> {
    Collection<Comment> findAllByBook_Id(String bookId);
}
