package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class JpaCommentRepository implements CommentRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<Comment> add(long bookId, String text) {
        Book book = em.find(Book.class, bookId);

        if (book == null) return Optional.empty();

        Comment comment = new Comment();
        comment.setText(text);
        comment.setBook(book);

        book.getComments().add(comment);
        em.persist(comment);
        return Optional.of(comment);
    }

    @Override
    public void update(long commentId, String text) {
        Comment comment = em.find(Comment.class, commentId);
        comment.setText(text);
    }

    @Override
    public Optional<Comment> findById(long id) {
        Comment comment = em.find(Comment.class, id);
        if (comment == null) {
            return Optional.empty();
        } else {
            return Optional.of(comment);
        }
    }

    @Override
    public List<Comment> findAllForBook(long bookId) {
        TypedQuery<Comment> query = em.createQuery(
                "select c from Comment c where c.book.id = :bookId",
                Comment.class);
        query.setParameter("bookId", bookId);
        return query.getResultList();
    }

    @Override
    public void deleteById(long id) {
        Comment comment = em.find(Comment.class, id);
        em.remove(comment);
    }
}
