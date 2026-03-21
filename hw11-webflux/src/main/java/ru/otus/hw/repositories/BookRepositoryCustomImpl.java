package ru.otus.hw.repositories;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Repository
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private final R2dbcEntityOperations template;

    private final GenreRepository genreRepository;

    @Override
    public Flux<Book> findAllWithAuthorAndGenres() {
        return getAllBookIdToGenres()
                .map(list -> list.stream()
                        .collect(Collectors.toMap(
                                BookIdToGenres::bookId,
                                BookIdToGenres::genres
                        )))
                .flatMapMany(bookIdToGenres ->
                        findAllWithAuthor()
                                .map(book -> {
                                    book.setGenres(bookIdToGenres.get(book.getId()));
                                    return book;
                                })
                );
    }


    @Override
    public Mono<Book> findByIdWithAuthorAndGenres(long id) {
        String sql = """
                SELECT 
                    b.id as b_id,
                    b.title as b_title,
                    a.id as a_id,
                    a.full_name as a_name,
                    g.id as g_id,
                    g.name as g_name
                FROM books b
                JOIN authors a ON b.author_id = a.id
                LEFT JOIN books_genres bg ON bg.book_id = b.id
                LEFT JOIN genres g ON g.id = bg.genre_id
                WHERE b.id = :id
                ORDER BY b.id
                """;

        return queryBooks(sql, Map.of("id", id))
                .collectList()
                .flatMap(this::aggregateSingle);
    }

    @Override
    public Mono<Book> save(Book book) {
        if (book.getId() == 0) {
            // Новая книга - INSERT
            return insertBook(book)
                    .flatMap(bookId -> insertBookGenres(bookId, book.getGenres())
                            .thenReturn(bookId))
                    .map(bookId -> {
                        book.setId(bookId);
                        return book;
                    });
        } else {
            // Обновление книги - UPDATE + перезапись жанров
            return updateBook(book)
                    .flatMap(updated -> deleteAndInsertBookGenres(book.getId(), book.getGenres())
                            .thenReturn(updated));
        }
    }

    public record BookIdToGenreId(long bookId, long genreId) {
    }

    public record BookIdToGenresIds(long bookId, List<Long> genresIds) {
    }

    public record BookIdToGenres(long bookId, List<Genre> genres) {
    }

    private Flux<Book> findAllWithAuthor() {
        String sql = """
                SELECT 
                    b.id as b_id,
                    b.title as b_title,
                    a.id as a_id,
                    a.full_name as a_name
                FROM books b
                JOIN authors a ON b.author_id = a.id
                ORDER BY b.id
                """;

        return template.getDatabaseClient().sql(sql)
                .map((row, metadata) -> new Book(
                        row.get("b_id", Long.class),
                        row.get("b_title", String.class),
                        new Author(
                                row.get("a_id", Long.class),
                                row.get("a_name", String.class)
                        ),
                        Collections.emptyList()
                ))
                .all();
    }

    private Flux<BookIdToGenreId> getAllBookIdToGenreId() {
        var sql = "select book_id, genre_id from books_genres";
        return template.getDatabaseClient().sql(sql)
                .map((row, metadata) -> new BookIdToGenreId(
                                row.get("book_id", Long.class),
                                row.get("genre_id", Long.class)
                        )
                ).all();
    }

    private Mono<List<BookIdToGenres>> getAllBookIdToGenres() {
        return getBookIdToGenresIds()
                .zipWith(genreRepository.findAll().collectList())
                .map(tuple -> {
                    Map<Long, Genre> genresMap = tuple.getT2().stream()
                            .collect(Collectors.toMap(Genre::getId, Function.identity()));
                    return tuple.getT1().map(v -> {
                                List<Genre> genres = v.genresIds.stream()
                                        .map(genresMap::get)
                                        .toList();
                                return new BookIdToGenres(v.bookId, genres);
                            }
                    ).toList();
                });
    }

    private Mono<Stream<BookIdToGenresIds>> getBookIdToGenresIds() {
        return getAllBookIdToGenreId().collectList()
                .map(list ->
                        list.stream()
                                .collect(Collectors.groupingBy(BookIdToGenreId::bookId))
                                .entrySet()
                                .stream()
                                .map(e ->
                                        new BookIdToGenresIds(
                                                e.getKey(),
                                                e.getValue().stream().map(BookIdToGenreId::genreId).toList()
                                        ))
                );
    }

    private Mono<Book> updateBook(Book book) {
        return template.getDatabaseClient().sql("""
                        update books set title = :title, author_id = :authorId
                        where id = :id
                        """)
                .bind("id", book.getId())
                .bind("title", book.getTitle())
                .bind("authorId", book.getAuthor().getId())
                .fetch()
                .rowsUpdated()
                .then(Mono.just(book));
    }

    private Mono<Void> deleteAndInsertBookGenres(Long bookId, List<Genre> genres) {
        return deleteBookGenres(bookId)
                .then(insertBookGenres(bookId, genres));
    }

    private Mono<Void> deleteBookGenres(Long bookId) {
        return template.getDatabaseClient().sql("""
                        delete from books_genres where book_id = :bookId
                        """)
                .bind("bookId", bookId)
                .fetch()
                .rowsUpdated()
                .then();
    }

    private Flux<FlatRow> queryBooks(String sql, Map<String, Object> params) {
        var client = template.getDatabaseClient().sql(sql);

        if (params != null) {
            for (var entry : params.entrySet()) {
                client = client.bind(entry.getKey(), entry.getValue());
            }
        }

        return client
                .map(mapRow())
                .all();
    }

    private Mono<Long> insertBook(Book book) {
        return template.getDatabaseClient().sql("""
                        insert into books(title, author_id)
                        values (:title, :authorId)
                        """)
                .bind("title", book.getTitle())
                .bind("authorId", book.getAuthor().getId())
                .fetch()
                .rowsUpdated()
                .then(template.getDatabaseClient().sql("""
                                select id from books
                                where title = :title and author_id = :authorId
                                order by id desc
                                limit 1
                                """)
                        .bind("title", book.getTitle())
                        .bind("authorId", book.getAuthor().getId())
                        .map((row, meta) -> row.get("id", Long.class))
                        .one()
                );
    }

    private Mono<Void> insertBookGenres(Long bookId, List<Genre> genres) {
        return Flux.fromIterable(genres)
                .flatMap(genre ->
                        template.getDatabaseClient().sql("""
                                        insert into books_genres(book_id, genre_id)
                                        values (:bookId, :genreId)
                                        """)
                                .bind("bookId", bookId)
                                .bind("genreId", genre.getId())
                                .fetch()
                                .rowsUpdated()
                )
                .then();
    }


    private static BiFunction<Row, RowMetadata, FlatRow> mapRow() {
        return (row, metadata) -> new FlatRow(
                row.get("b_id", Long.class),
                row.get("b_title", String.class),
                row.get("a_id", Long.class),
                row.get("a_name", String.class),
                row.get("g_id", Long.class),
                row.get("g_name", String.class)
        );
    }

    record FlatRow(
            Long bookId,
            String bookTitle,
            Long authorId,
            String authorName,
            Long genreId,
            String genreName
    ) {
    }

    private Mono<Book> aggregateSingle(List<FlatRow> rows) {

        if (rows.isEmpty()) {
            return Mono.empty();
        }
        FlatRow first = rows.get(0);
        Author author = new Author(first.authorId(), first.authorName());
        List<Genre> genres = new ArrayList<>();

        for (FlatRow row : rows) {
            if (row.genreId() != null) {
                genres.add(new Genre(row.genreId(), row.genreName()));
            }
        }

        Book book = new Book(
                first.bookId(),
                first.bookTitle(),
                author,
                genres
        );

        return Mono.just(book);
    }

}
