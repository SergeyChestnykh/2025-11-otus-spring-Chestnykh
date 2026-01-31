package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findAllForBook(long bookId) {
        return commentRepository.findAllForBook(bookId);
    }

    @Override
    @Transactional
    public Optional<Comment> find(long commentId) {
        return commentRepository.findById(commentId);
    }

    @Override
    @Transactional
    public Comment insert(long bookId, String text) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id:" + bookId));

        Comment comment = new Comment();
        comment.setText(text);
        comment.setBook(book);

        book.getComments().set(0, comment);

        return commentRepository.save(comment);
    }

    @Override
    public Comment update(long commentId, String text) {
        return null;
    }

    @Override
    public void deleteById(long id) {

    }
}
