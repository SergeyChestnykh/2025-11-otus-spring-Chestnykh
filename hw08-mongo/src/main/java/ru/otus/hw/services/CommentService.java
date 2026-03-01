package ru.otus.hw.services;


import ru.otus.hw.dto.CommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    List<CommentDto> findAllForBook(String bookId);

    Optional<CommentDto> find(String id);

    CommentDto insert(String bookId, String text);

    CommentDto update(String id, String text);

    void deleteById(String id);
}
