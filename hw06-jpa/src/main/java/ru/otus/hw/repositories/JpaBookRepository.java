package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<Book> findById(long id) {
        var query = em.createQuery(
                "select distinct b from Book b " +
                        "join fetch b.author " +
                        "join fetch b.genres " +
                        "where b.id = :id",
                Book.class
        ).setParameter("id", id);
        List<Book> results = query.getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        }
        var book = results.get(0);
        return Optional.of(book);
    }

    @Override
    public List<Book> findAll() {
        return em.createQuery(
                        "select distinct b from Book b " +
                                "join fetch b.author " +
                                "join fetch b.genres "
                        , Book.class)
                .getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            persistAuthorIfNeeded(book);
            persistGenresIfNeeded(book);
            em.persist(book);
            return book;
        } else {
            return em.merge(book);
        }
    }

    private void persistAuthorIfNeeded(Book book) {
        var author = book.getAuthor();
        if (author != null && author.getId() == 0) {
            em.persist(author);
        }
    }

    private void persistGenresIfNeeded(Book book) {
        var genres = book.getGenres();
        if (genres != null) {
            for (var genre : genres) {
                if (genre.getId() == 0) {
                    em.persist(genre);
                }
            }
        }
    }

    @Override
    public void deleteById(long id) {
        Book book = em.find(Book.class, id);
        em.remove(book);
    }
}
