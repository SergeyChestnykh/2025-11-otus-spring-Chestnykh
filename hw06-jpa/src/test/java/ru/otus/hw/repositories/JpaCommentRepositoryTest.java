package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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

    @Test
    @DisplayName(" должен добавить комментарий к книге")
    void add() {
        String myCommentText = "My comment";
        Comment returnedComment = jpaCommentRepository.add(1, myCommentText).orElseThrow();

        assertThat(returnedComment.getText()).isEqualTo(myCommentText);
        assertThat(jpaCommentRepository.findById(returnedComment.getId()))
                .isPresent()
                .get()
                .usingRecursiveAssertion()
                .ignoringFields("id")
                .isEqualTo(returnedComment);
    }

    @Test
    @DisplayName(" должен получить все комментарии для определенной книги")
    void findAllForBook() {
        var actualComments = jpaCommentRepository.findAllForBook(1);
        var expectedBook = new Book();
        var expectedComments = List.of(
                new Comment(1, "First comment b1", expectedBook),
                new Comment(2, "Second comment b1", expectedBook)
        );

        assertThat(actualComments)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .ignoringFields("book")
                .isEqualTo(expectedComments);
    }

    @Test
    @DisplayName(" должен обновить комментарий по id")
    void update() {
        Comment comment = jpaCommentRepository.findById(1).orElseThrow();
        String newCommentText = "New comment";

        jpaCommentRepository.update(comment.getId(), newCommentText);


        Comment updatedComment = jpaCommentRepository.findById(comment.getId())
                .orElseThrow(() -> new AssertionError("Комментарий не найден после обновления"));

        assertThat(updatedComment.getText())
                .isEqualTo(newCommentText);
    }

    @Test
    @DisplayName("должен удалить комментарий по id")
    void deleteById() {
        String myCommentText = "My comment";
        Comment returnedComment = jpaCommentRepository.add(1, myCommentText).orElseThrow();

        assertThat(jpaCommentRepository.findById(returnedComment.getId())).isPresent();

        jpaCommentRepository.deleteById(returnedComment.getId());

        assertThat(jpaCommentRepository.findById(returnedComment.getId())).isEmpty();
    }
}