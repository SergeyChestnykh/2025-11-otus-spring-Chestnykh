package ru.otus.hw.services;


import ru.otus.hw.dto.CommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    List<CommentDto> findAllForBook(long bookId);

    Optional<CommentDto> find(long id);

    CommentDto insert(long bookId, String text);

    CommentDto update(long id, String text);

    void deleteById(long id);
}
