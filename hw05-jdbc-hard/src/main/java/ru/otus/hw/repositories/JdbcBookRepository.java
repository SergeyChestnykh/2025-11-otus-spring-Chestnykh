package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

        String sql = """
                    SELECT 
                        b.id AS book_id,
                        b.title,
                        a.id AS author_id,
                        a.full_name AS author_name,
                        g.id AS genre_id,
                        g.name AS genre_name
                    FROM books b
                    JOIN authors a ON b.author_id = a.id
                    LEFT JOIN books_genres bg ON b.id = bg.book_id
                    LEFT JOIN genres g ON bg.genre_id = g.id
                    WHERE b.id = :id
                """;

        Book book = jdbcOperations.query(
                sql,
                Map.of("id", id),
                new BookResultSetExtractor()
        );

        return Optional.ofNullable(book);
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
        String sql = "DELETE FROM books WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        jdbcOperations.update(sql, params);
        removeGenresRelationsForBookId(id);
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

        String sql = """
                    INSERT INTO books(title, author_id)
                    VALUES (:title, :authorId)
                """;

        Map<String, Object> params = Map.of(
                "title", book.getTitle(),
                "authorId", book.getAuthor().getId()
        );

        jdbcOperations.update(sql, new MapSqlParameterSource(params), keyHolder, new String[]{"id"});

        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        String sql = """
                    UPDATE books
                    SET title = :title, author_id = :authorId
                    WHERE id = :id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", book.getId())
                .addValue("title", book.getTitle())
                .addValue("authorId", book.getAuthor().getId());

        int updatedRows = jdbcOperations.update(sql, params);

        if (updatedRows == 0) {
            throw new EntityNotFoundException("Book with id " + book.getId() + " not found");
        }

        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        MapSqlParameterSource[] batchParams = book.getGenres().stream()
                .map(genre ->
                        new MapSqlParameterSource()
                                .addValue("bookId", book.getId())
                                .addValue("genreId", genre.getId())
                )
                .toArray(MapSqlParameterSource[]::new);

        jdbcOperations.batchUpdate(
                "INSERT INTO books_genres(book_id, genre_id) VALUES (:bookId, :genreId)",
                batchParams
        );
    }

    private void removeGenresRelationsFor(Book book) {
        String sql = "DELETE FROM books_genres WHERE book_id = :bookId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("bookId", book.getId());

        jdbcOperations.update(sql, params);
    }

    private void removeGenresRelationsForBookId(long bookId) {
        String sql = "DELETE FROM books_genres WHERE book_id = :bookId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("bookId", bookId);

        jdbcOperations.update(sql, params);
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

    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException {

            Book book = null;
            List<Genre> genres = new ArrayList<>();

            while (rs.next()) {

                if (book == null) {
                    book = mapBook(rs);
                }

                addGenre(rs, genres);
            }

            if (book != null) {
                book.setGenres(genres);
            }

            return book;
        }

        private Book mapBook(ResultSet rs) throws SQLException {

            Author author = new Author(
                    rs.getLong("author_id"),
                    rs.getString("author_name")
            );

            return new Book(
                    rs.getLong("book_id"),
                    rs.getString("title"),
                    author,
                    new ArrayList<>()
            );
        }

        private void addGenre(ResultSet rs, List<Genre> genres) throws SQLException {

            long genreId = rs.getLong("genre_id");

            if (!rs.wasNull()) {
                genres.add(new Genre(
                        genreId,
                        rs.getString("genre_name")
                ));
            }
        }
    }


    private record BookGenreRelation(long bookId, long genreId) {
    }
}
