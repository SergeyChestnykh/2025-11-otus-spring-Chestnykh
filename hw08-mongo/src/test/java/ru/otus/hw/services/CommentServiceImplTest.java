package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Import({
        CommentServiceImpl.class,
        CommentConverter.class,
        BookConverter.class,
        GenreConverter.class,
        AuthorConverter.class
})
@DataMongoTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class CommentServiceImplTest {

    @Autowired
    private CommentService commentService;

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
    void findAllForBook() {
        assertThatCode(() -> {
            List<CommentDto> all = commentService.findAllForBook("1");
            assertThat(all.stream().map(CommentDto::text).toList())
                    .containsExactlyInAnyOrder("First comment b1", "Second comment b1");
        }).doesNotThrowAnyException();
    }

    @Test
    void find() {
        assertThatCode(() -> {
            Optional<CommentDto> comment = commentService.find("1");
            assertThat(comment).isNotEmpty();
            assertThat(comment.orElseThrow().text()).isNotEmpty();
        }).doesNotThrowAnyException();
    }

    @Test
    void insert() {
        assertThatCode(() -> {
            CommentDto newComment = commentService.insert("1", "New comment");

            CommentDto comment = commentService.find(newComment.id()).orElseThrow();
            assertThat(comment.text()).isEqualTo("New comment");
        }).doesNotThrowAnyException();
    }

    @Test
    void update() {
        assertThatCode(() -> {
            String updatedText = "Updated comment text";

            CommentDto updatedComment = commentService.update("1", updatedText);

            CommentDto comment = commentService.find(updatedComment.id()).orElseThrow();
            assertThat(comment.text()).isEqualTo(updatedText);
        }).doesNotThrowAnyException();
    }

    @Test
    void delete() {
        assertThatCode(() -> {
            assertThat(commentService.find("1")).isNotEmpty();

            commentService.deleteById("1");

            assertThat(commentService.find("1")).isEmpty();
        }).doesNotThrowAnyException();
    }

    @Test
    void updateBook_shouldReflectInComment() {
        Book bookToUpdate = mongoTemplate.findById("1", Book.class);
        String newBookTitle = "Updated_BookTitle_1";
        bookToUpdate.setTitle(newBookTitle);
        mongoTemplate.save(bookToUpdate);

        Book updatedBookInDb = mongoTemplate.findById("1", Book.class);
        assertEquals(newBookTitle, updatedBookInDb.getTitle(),
                "Книга должна быть обновлена в базе данных");

        Comment commentFromDb = mongoTemplate.findById("1", Comment.class);
        String commentBookTitle = commentFromDb.getBook().getTitle();

        assertEquals(newBookTitle, commentBookTitle,
                "Книга в комментарии должна быть обновлена после изменения в коллекции books");
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