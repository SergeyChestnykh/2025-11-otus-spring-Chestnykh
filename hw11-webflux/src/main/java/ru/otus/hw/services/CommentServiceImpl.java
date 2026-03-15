package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentConverter commentConverter;

    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public Flux<CommentDto> findAllForBook(long bookId) {
        return commentRepository.findAllByBookId(bookId)
                .map(commentConverter::commentToDto);
    }

    @Override
    @Transactional
    public Mono<CommentDto> find(long id) {
        return commentRepository.findById(id).map(commentConverter::commentToDto);
    }

    @Override
    @Transactional
    public Mono<CommentDto> insert(long bookId, String text) {
        return bookRepository.findById(bookId)
                .flatMap(book -> {
                    Comment comment = new Comment();
                    comment.setText(text);
                    comment.setBookId(book.getId());
                    return commentRepository.save(comment).map(commentConverter::commentToDto);
                });
    }

    @Override
    @Transactional
    public Mono<CommentDto> update(long id, String text) {
        return commentRepository.findById(id)
                .flatMap(comment -> {
                    comment.setText(text);
                    return commentRepository.save(comment).map(commentConverter::commentToDto);
                })
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Comment not found with id: " + id)));
    }


    @Override
    @Transactional
    public Mono<Void> deleteById(long id) {
        return commentRepository.deleteById(id);
    }
}
