package ru.otus.hw.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.ArrayList;
import java.util.List;

@ChangeLog
public class DatabaseChangelog {

    private final List<Author> authors = new ArrayList<>();
    private final List<Genre> genres = new ArrayList<>();
    private final List<Book> books = new ArrayList<>();

    @ChangeSet(order = "001", id = "dropDb", author = "Chestnykh", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "insertAuthors", author = "Chestnykh")
    public void insertAuthors(AuthorRepository authorRepository) {
        authors.add(authorRepository.save(new Author(null, "Author_1")));
        authors.add(authorRepository.save(new Author(null, "Author_2")));
        authors.add(authorRepository.save(new Author(null, "Author_3")));
    }

    @ChangeSet(order = "003", id = "insertGenres", author = "Chestnykh")
    public void insertGenres(GenreRepository genreRepository) {
        genres.add(genreRepository.save(new Genre(null, "Genre_1")));
        genres.add(genreRepository.save(new Genre(null, "Genre_2")));
        genres.add(genreRepository.save(new Genre(null, "Genre_3")));
        genres.add(genreRepository.save(new Genre(null, "Genre_4")));
        genres.add(genreRepository.save(new Genre(null, "Genre_5")));
        genres.add(genreRepository.save(new Genre(null, "Genre_6")));
    }

    @ChangeSet(order = "004", id = "insertBooks", author = "Chestnykh")
    public void insertBooks(BookRepository bookRepository) {
        Book book1 = new Book(null, "BookTitle_1", authors.get(0),
                List.of(genres.get(0), genres.get(1)));
        books.add(bookRepository.save(book1));

        Book book2 = new Book(null, "BookTitle_2", authors.get(1),
                List.of(genres.get(2), genres.get(3)));
        books.add(bookRepository.save(book2));

        Book book3 = new Book(null, "BookTitle_3", authors.get(2),
                List.of(genres.get(4), genres.get(5)));
        books.add(bookRepository.save(book3));
    }

    @ChangeSet(order = "005", id = "insertComments", author = "Chestnykh")
    public void insertComments(CommentRepository commentRepository) {
        commentRepository.save(new Comment(null, "First comment b1", books.get(0)));
        commentRepository.save(new Comment(null, "Second comment b1", books.get(0)));
        commentRepository.save(new Comment(null, "First comment b2", books.get(1)));
    }
}
