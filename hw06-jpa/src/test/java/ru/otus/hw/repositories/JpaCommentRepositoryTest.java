package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий комментариев ")
@DataJpaTest
@Import(JpaCommentRepository.class)
class JpaCommentRepositoryTest {

    @Autowired
    private JpaCommentRepository jpaCommentRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName(" должен добавить комментарий к книге")
    void add() {
        String myCommentText = "My comment";

        Comment returnedComment = jpaCommentRepository.add(1, myCommentText).orElseThrow();

        assertThat(returnedComment.getText()).isEqualTo(myCommentText);
        Comment comment = em.find(Comment.class, returnedComment.getId());
        assertThat(comment)
                .usingRecursiveAssertion()
                .ignoringFields("id")
                .isEqualTo(returnedComment);
    }

    @Test
    @DisplayName(" должен получить все комментарии для определенной книги")
    void findAllForBook() {
        var expectedBook = new Book();
        var expectedComments = List.of(
                new Comment(1, "First comment b1", expectedBook),
                new Comment(2, "Second comment b1", expectedBook)
        );

        var actualComments = jpaCommentRepository.findAllForBook(1);

        assertThat(actualComments)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .ignoringFields("book")
                .isEqualTo(expectedComments);
    }

    @Test
    @DisplayName(" должен обновить комментарий по id")
    void update() {
        Comment comment = em.find(Comment.class, 1);
        String newCommentText = "New comment";

        jpaCommentRepository.update(comment.getId(), newCommentText);

        Comment updatedComment = em.find(Comment.class, 1);
        assertThat(updatedComment.getText())
                .isEqualTo(newCommentText);
    }

    @Test
    @DisplayName("должен удалить комментарий по id")
    void deleteById() {
        Comment commentForDelete = em.find(Comment.class, 1);
        assertThat(commentForDelete).isNotNull();

        jpaCommentRepository.deleteById(commentForDelete.getId());

        assertThat(em.find(Comment.class, commentForDelete.getId())).isNull();
    }
}