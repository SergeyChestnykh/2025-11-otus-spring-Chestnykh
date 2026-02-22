package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.CommentDto;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@Import({
        CommentServiceImpl.class,
        CommentConverter.class,
        BookConverter.class,
        GenreConverter.class,
        AuthorConverter.class
})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CommentServiceImplTest {

    @Autowired
    private CommentServiceImpl commentService;

    @Test
    void findAllForBook() {
        assertThatCode(() -> {
            List<CommentDto> all = commentService.findAllForBook(1L);
            assertThat(all.stream().map(CommentDto::text).toList())
                    .containsExactlyInAnyOrder("First comment b1", "Second comment b1");
        }).doesNotThrowAnyException();
    }

    @Test
    void find() {
        assertThatCode(() -> {
            Optional<CommentDto> comment = commentService.find(1L);
            assertThat(comment).isNotEmpty();
            assertThat(comment.orElseThrow().text()).isNotEmpty();
        }).doesNotThrowAnyException();
    }

    @Test
    void insert() {
        assertThatCode(() -> {
            CommentDto newComment = commentService.insert(1L, "New comment");

            CommentDto comment = commentService.find(newComment.id()).orElseThrow();
            assertThat(comment.text()).isEqualTo("New comment");
            assertThat(comment.id()).isGreaterThan(0);
        }).doesNotThrowAnyException();
    }

    @Test
    void update() {
        assertThatCode(() -> {
            String updatedText = "Updated comment text";

            CommentDto updatedComment = commentService.update(1L, updatedText);

            CommentDto comment = commentService.find(updatedComment.id()).orElseThrow();
            assertThat(comment.text()).isEqualTo(updatedText);
        }).doesNotThrowAnyException();
    }

    @Test
    void delete() {
        assertThatCode(() -> {
            assertThat(commentService.find(1L)).isNotEmpty();

            commentService.deleteById(1L);

            assertThat(commentService.find(1L)).isEmpty();
        }).doesNotThrowAnyException();
    }
}