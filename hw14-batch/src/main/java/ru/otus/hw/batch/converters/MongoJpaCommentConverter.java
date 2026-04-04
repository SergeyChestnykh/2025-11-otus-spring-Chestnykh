package ru.otus.hw.batch.converters;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.batch.cache.MongoIdRelationCache;
import ru.otus.hw.jpa.models.Book;
import ru.otus.hw.jpa.models.Comment;

@Component
@AllArgsConstructor
public class MongoJpaCommentConverter {
    private final MongoIdRelationCache<Book> mongoBookIdRelationCache;

    public Comment toCommentJpa(ru.otus.hw.mongo.models.Comment mongoComment) {
        Comment comment = new Comment();
        comment.setText(mongoComment.getText());
        comment.setBook(mongoBookIdRelationCache.get(mongoComment.getBook().getId()));
        return comment;
    }
}