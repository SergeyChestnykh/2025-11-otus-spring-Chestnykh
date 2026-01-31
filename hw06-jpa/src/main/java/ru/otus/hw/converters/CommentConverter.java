package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.models.Comment;

import java.util.Objects;

@Component
public class CommentConverter {

    public String commentToString(Comment comment) {
        return "Id: %d, text: '%s', book: %s".formatted(
                comment.getId(),
                Objects.toString(comment.getText(), ""),
                comment.getBook().getTitle()
        );
    }
}