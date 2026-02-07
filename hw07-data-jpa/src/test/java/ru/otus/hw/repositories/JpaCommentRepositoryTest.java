package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий комментариев ")
@DataJpaTest
class JpaCommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName(" должен добавить комментарий к книге")
    void add() {
        Comment comment = new Comment();
        comment.setText("My comment");

        Comment returnedComment = commentRepository.save(comment);

        assertThat(returnedComment.getText()).isEqualTo(comment.getText());
        assertThat(em.find(Comment.class, returnedComment.getId()))
                .usingRecursiveAssertion()
                .ignoringFields("id")
                .isEqualTo(comment);
    }

    @Test
    @DisplayName(" должен получить все комментарии для определенной книги")
    void findAllForBook() {
        var expectedBook = new Book();
        var expectedComments = List.of(
                new Comment(1, "First comment b1", expectedBook),
                new Comment(2, "Second comment b1", expectedBook)
        );

        var actualComments = commentRepository.findAllByBookId(1);

        assertThat(actualComments)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .ignoringFields("book")
                .isEqualTo(expectedComments);
    }

    @Test
    @DisplayName("должен удалить комментарий по id")
    void deleteById() {
        Comment commentForDelete = em.find(Comment.class, 1);
        assertThat(commentForDelete).isNotNull();

        commentRepository.deleteById(commentForDelete.getId());

        assertThat(em.find(Comment.class, commentForDelete.getId())).isNull();
    }
}