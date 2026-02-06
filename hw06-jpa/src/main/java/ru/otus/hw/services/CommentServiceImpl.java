package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentConverter commentConverter;

    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findAllForBook(long bookId) {
        return commentRepository.findAllForBook(bookId).stream()
                .map(commentConverter::commentToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<CommentDto> find(long id) {
        return commentRepository.findById(id).map(commentConverter::commentToDto);
    }

    @Override
    @Transactional
    public CommentDto insert(long bookId, String text) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id:" + bookId));

        Comment comment = new Comment();
        comment.setText(text);
        comment.setBook(book);

        return commentConverter.commentToDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentDto update(long id, String text) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id:" + id));
        comment.setText(text);
        return commentConverter.commentToDto(commentRepository.save(comment));
    }


    @Override
    @Transactional
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }
}
