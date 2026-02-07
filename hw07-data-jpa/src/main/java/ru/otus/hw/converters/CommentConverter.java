package ru.otus.hw.converters;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.Objects;

@Component
@AllArgsConstructor
public class CommentConverter {

    private final BookConverter bookConverter;

    public String commentToString(CommentDto commentDto) {
        return "Id: %d, text: '%s', book: %s".formatted(
                commentDto.id(),
                Objects.toString(commentDto.text(), ""),
                commentDto.bookDto().title()
        );
    }

    public CommentDto commentToDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                bookConverter.bookToDto(comment.getBook())
        );
    }

    @SuppressWarnings("unused")
    public Comment dtoToComment(CommentDto commentDto, Book book) {
        Comment comment = new Comment();
        if (commentDto.id() != 0) {
            comment.setId(commentDto.id());
        }
        comment.setText(commentDto.text());
        comment.setBook(book);
        return comment;
    }

}