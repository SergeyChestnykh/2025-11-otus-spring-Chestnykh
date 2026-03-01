package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий комментариев ")
@DataMongoTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(Comment.class);
        mongoTemplate.dropCollection(Book.class);
        var dbBooks = getDbBooks();
        for (var book : dbBooks) {
            mongoTemplate.save(book);
        }
        var dbComments = getDbComments();
        for (var comment : dbComments) {
            mongoTemplate.save(comment);
        }
    }

    @Test
    @DisplayName(" должен добавить комментарий к книге")
    void add() {
        Comment comment = new Comment();
        comment.setText("My comment");

        Comment returnedComment = commentRepository.save(comment);

        assertThat(returnedComment.getText()).isEqualTo(comment.getText());
        assertThat(mongoTemplate.findById(returnedComment.getId(), Comment.class))
                .usingRecursiveAssertion()
                .ignoringFields("id")
                .isEqualTo(comment);
    }

    @Test
    @DisplayName(" должен получить все комментарии для определенной книги")
    void findAllForBook() {
        var expectedBook = new Book();
        var expectedComments = List.of(
                new Comment("1", "First comment b1", expectedBook),
                new Comment("2", "Second comment b1", expectedBook)
        );

        var actualComments = commentRepository.findAllByBook_Id("1");

        assertThat(actualComments)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .ignoringFields("book")
                .isEqualTo(expectedComments);
    }

    @Test
    @DisplayName("должен удалить комментарий по id")
    void deleteById() {
        Comment commentForDelete = mongoTemplate.findById("1", Comment.class);
        assertThat(commentForDelete).isNotNull();

        commentRepository.deleteById(commentForDelete.getId());

        assertThat(mongoTemplate.findById(commentForDelete.getId(), Comment.class)).isNull();
    }

    private static List<Comment> getDbComments() {
        Book book1 = new Book("1", "BookTitle_1", null, List.of());
        return List.of(
                new Comment("1", "First comment b1", book1),
                new Comment("2", "Second comment b1", book1)
        );
    }

    private static List<Book> getDbBooks() {
        Author author1 = new Author("1", "Author_1");
        Genre genre1 = new Genre("1", "Genre_1");
        Book book1 = new Book("1", "BookTitle_1", author1, List.of(genre1));
        return List.of(book1);
    }
}