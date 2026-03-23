package ru.otus.hw.batch.converters;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.jpa.models.Book;
import ru.otus.hw.jpa.models.Comment;

import java.util.Map;

@Component
@AllArgsConstructor
public class MongoJpaCommentConverter {

    private final Map<String, Book> bookRelationsHolder;

    public Comment toCommentJpa(ru.otus.hw.mongo.models.Comment mongoComment) {
        Comment comment = new Comment();
        comment.setText(mongoComment.getText());
        comment.setBook(bookRelationsHolder.get(mongoComment.getBook().getId()));
        return comment;
    }
}