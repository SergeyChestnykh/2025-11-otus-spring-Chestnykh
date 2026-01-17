package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final GenreRepository genreRepository;

    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public Optional<Book> findById(long id) {
        return Optional.empty();
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var books = getAllBooksWithoutGenres();
        var relations = getAllGenreRelations();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        //...
    }

    private List<Book> getAllBooksWithoutGenres() {
        String sql = """
                    SELECT b.id AS book_id, b.title, a.id AS author_id, a.full_name AS author_name
                    FROM books b
                    JOIN authors a ON b.author_id = a.id
                """;

        return jdbcOperations.query(sql, new BookRowMapper());
    }


    private List<BookGenreRelation> getAllGenreRelations() {
        return jdbcOperations.query(
                "select book_id, genre_id from books_genres",
                (rs, rowNum) -> {
                    long bookId = rs.getLong("book_id");
                    long genreId = rs.getLong("genre_id");
                    return new BookGenreRelation(bookId, genreId);
                }
        );
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres,
                                List<Genre> genres,
                                List<BookGenreRelation> relations) {
        Map<Long, Genre> genreById = genres.stream()
                .collect(Collectors.toMap(Genre::getId, g -> g));

        booksWithoutGenres.forEach(book -> {
                    List<Genre> bookGenres = relations.stream()
                            .filter(relation -> relation.bookId() == book.getId())
                            .map(relation -> genreById.get(relation.genreId()))
                            .filter(Objects::nonNull)
                            .toList();
                    book.setGenres(bookGenres);
                }
        );
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();

        //...

        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        //...

        // Выбросить EntityNotFoundException если не обновлено ни одной записи в БД
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        // Использовать метод batchUpdate
    }

    private void removeGenresRelationsFor(Book book) {
        //...
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            {
                Author author = new Author(rs.getLong("author_id"), rs.getString("author_name"));
                return new Book(rs.getLong("book_id"), rs.getString("title"), author, List.of());
            }
        }
    }

    // Использовать для findById
    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            return null;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
