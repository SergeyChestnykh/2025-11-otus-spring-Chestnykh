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
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void findAllForBook() {
        List<CommentDto> all = commentService.findAllForBook(1L);

        assertThat(all.stream().map(CommentDto::text).toList())
                .containsExactlyInAnyOrder("First comment b1", "Second comment b1");

        assertThatCode(
                () -> all.forEach(comment -> comment.bookDto().title())
        ).doesNotThrowAnyException();
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void find() {
        Optional<CommentDto> comment = commentService.find(1L);
        assertThatCode(
                () -> comment.orElseThrow().bookDto().title()
        ).doesNotThrowAnyException();
    }

    @Test
    void insert() {
        CommentDto newComment = commentService.insert(1L, "New comment");

        CommentDto comment = commentService.find(newComment.id()).orElseThrow();
        assertThat(comment.text()).isEqualTo("New comment");
        assertThat(comment.bookDto().id()).isEqualTo(1L);

    }

    @Test
    void update() {
        String updatedText = "Updated comment text";

        CommentDto updatedComment = commentService.update(1L, updatedText);

        CommentDto comment = commentService.find(updatedComment.id()).orElseThrow();
        assertThat(comment.text()).isEqualTo(updatedText);
        assertThat(comment.bookDto().id()).isEqualTo(1L);
    }

    @Test
    void delete() {
        assertThat(commentService.find(1L)).isNotEmpty();

        commentService.deleteById(1L);

        assertThat(commentService.find(1L)).isEmpty();
    }
}