package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
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
                        "where b.id = :id",
                Book.class
        );
        query.setParameter("id", id);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Book> findAll() {
        return em.createQuery(
                        "select distinct b from Book b " +
                                "join fetch b.author "
                        , Book.class)
                .getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            if (book.getAuthor() != null && book.getAuthor().getId() == 0) {
                em.persist(book.getAuthor());
            }

            if (book.getGenres() != null) {
                book.getGenres().forEach(genre -> {
                    if (genre.getId() == 0) {
                        em.persist(genre);
                    }
                });
            }

            em.persist(book);
            return book;
        } else {
            return em.merge(book);
        }
    }

    @Override
    public void deleteById(long id) {
        Query query = em.createQuery("delete from Book b where b.id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }
}
