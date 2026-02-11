package ru.otus.hw.converters;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;

import java.util.Objects;

@Component
@AllArgsConstructor
public class CommentConverter {

    private final BookConverter bookConverter;

    public String commentToString(CommentDto commentDto) {
        return "Id: %d, text: '%s".formatted(
                commentDto.id(),
                Objects.toString(commentDto.text(), "")
        );
    }

    public CommentDto commentToDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText()
        );
    }
}