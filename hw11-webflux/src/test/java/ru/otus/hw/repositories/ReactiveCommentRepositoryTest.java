package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@DisplayName("Репозиторий комментариев ")
@DataR2dbcTest
class ReactiveCommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private R2dbcEntityTemplate template;

    @Test
    @DisplayName(" должен добавить комментарий к книге")
    void add() {
        Comment comment = new Comment();
        comment.setText("My comment");

        Comment returnedComment = commentRepository.save(comment)
                .block();

        assertThat(returnedComment.getText()).isEqualTo(comment.getText());

        Comment fetchedComment = template.select(Comment.class)
                .matching(query(where("id").is(returnedComment.getId())))
                .one()
                .block();

        assertThat(fetchedComment)
                .usingRecursiveAssertion()
                .ignoringFields("id")
                .isEqualTo(comment);
    }

    @Test
    @DisplayName(" должен получить все комментарии для определенной книги")
    void findAllForBook() {
        var expectedBook = new Book();
        var expectedComments = List.of(
                new Comment(1, "First comment b1", expectedBook.getId()),
                new Comment(2, "Second comment b1", expectedBook.getId())
        );

        var actualComments = commentRepository.findAllByBookId(1).collectList().block();

        assertThat(actualComments)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .ignoringFields("bookId")
                .isEqualTo(expectedComments);
    }

    @Test
    @DisplayName("должен удалить комментарий по id")
    void deleteById() {
        Comment commentForDelete = template.select(Comment.class)
                .matching(query(where("id").is(1L)))
                .one()
                .block();
        assertThat(commentForDelete).isNotNull();

        commentRepository.deleteById(commentForDelete.getId()).block();

        Comment deletedComment = template.select(Comment.class)
                .matching(query(where("id").is(1L)))
                .one()
                .block();
        assertThat(deletedComment).isNull();
    }
}