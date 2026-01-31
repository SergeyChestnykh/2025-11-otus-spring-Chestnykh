package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
class CommentServiceImplTest {

    @Autowired
    private CommentServiceImpl commentService;

    @Test
    void findAllForBook() {
        List<Comment> all = commentService.findAllForBook(1L);

        assertThat(all.stream().map(Comment::getText).toList())
                .containsExactlyInAnyOrder("First comment b1", "Second comment b1");

        assertThatCode(
                () -> all.forEach(comment -> comment.getBook())
        ).doesNotThrowAnyException();
    }

    @Test
    void find() {
        Optional<Comment> comment = commentService.find(1L);
        assertThatCode(
                () -> comment.orElseThrow().getBook()
        ).doesNotThrowAnyException();
    }

    @Test
    void insert(){
        commentService.insert()
    }
}