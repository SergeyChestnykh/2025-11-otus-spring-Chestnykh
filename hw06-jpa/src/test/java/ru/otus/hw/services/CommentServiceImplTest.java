package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class CommentServiceImplTest {

    @Autowired
    private CommentServiceImpl commentService;

    @Test
    void findAllForBook() {
        List<Comment> all = commentService.findAllForBook(1L);

        assertThat(all.stream().map(Comment::getText).toList())
                .containsExactlyInAnyOrder("First comment b1", "Second comment b1");

        assertThatCode(
                () -> all.forEach(comment -> comment.getBook().getTitle())
        ).doesNotThrowAnyException();
    }

    @Test
    void find() {
        Optional<Comment> comment = commentService.find(1L);
        assertThatCode(
                () -> comment.orElseThrow().getBook().getTitle()
        ).doesNotThrowAnyException();
    }

    @Test
    void insert() {
        Comment newComment = commentService.insert(1L, "New comment");

        Comment comment = commentService.find(newComment.getId()).orElseThrow();
        assertThat(comment.getText()).isEqualTo("New comment");
        assertThat(comment.getBook().getId()).isEqualTo(1L);

    }

    @Test
    void update() {
        String updatedText = "Updated comment text";

        Comment updatedComment = commentService.update(1L, updatedText);

        Comment comment = commentService.find(updatedComment.getId()).orElseThrow();
        assertThat(comment.getText()).isEqualTo(updatedText);
        assertThat(comment.getBook().getId()).isEqualTo(1L);
    }

    @Test
    void delete() {
        assertThat(commentService.find(1L)).isNotEmpty();

        commentService.deleteById(1L);

        assertThat(commentService.find(1L)).isEmpty();
    }
}