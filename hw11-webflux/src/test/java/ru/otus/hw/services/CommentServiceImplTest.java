package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DataR2dbcTest
@Import({
        CommentServiceImpl.class,
        CommentConverter.class,
        BookConverter.class,
        GenreConverter.class,
        AuthorConverter.class,
})
class CommentServiceImplTest {

    @Autowired
    private CommentServiceImpl commentService;

    @MockitoBean
    private CommentRepository commentRepository;

    @MockitoBean
    private BookRepository bookRepository;

    @Autowired
    private CommentConverter commentConverter;

    @Test
    void findAllForBook() {
        Comment comment1 = new Comment(1L, "First comment b1", 1L);
        Comment comment2 = new Comment(2L, "Second comment b1", 1L);

        when(commentRepository.findAllByBookId(1L))
                .thenReturn(Flux.just(comment1, comment2));

        List<CommentDto> result = commentService.findAllForBook(1L).collectList().block();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).text()).isEqualTo("First comment b1");
        assertThat(result.get(1).text()).isEqualTo("Second comment b1");
        verify(commentRepository).findAllByBookId(1L);
    }

    @Test
    void find() {
        Comment comment = new Comment(1L, "First comment b1", 1L);
        CommentDto commentDto = commentConverter.commentToDto(comment);

        when(commentRepository.findById(1L)).thenReturn(Mono.just(comment));

        CommentDto result = commentService.find(1L).block();

        assertThat(result).isEqualTo(commentDto);
        verify(commentRepository).findById(1L);
    }

    @Test
    void insert() {
        Book book = new Book(1L, "BookTitle_1", null, List.of());
        Comment newComment = new Comment(0L, "New comment", 1L);
        Comment savedComment = new Comment(3L, "New comment", 1L);
        CommentDto commentDto = commentConverter.commentToDto(savedComment);

        when(bookRepository.findById(1L)).thenReturn(Mono.just(book));
        when(commentRepository.save(any(Comment.class))).thenReturn(Mono.just(savedComment));

        CommentDto result = commentService.insert(1L, "New comment").block();

        assertThat(result).isEqualTo(commentDto);
        assertThat(result.id()).isGreaterThan(0);
        verify(bookRepository).findById(1L);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void update() {
        Comment comment = new Comment(1L, "First comment b1", 1L);
        Comment updatedComment = new Comment(1L, "Updated comment text", 1L);
        CommentDto commentDto = commentConverter.commentToDto(updatedComment);

        when(commentRepository.findById(1L)).thenReturn(Mono.just(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(Mono.just(updatedComment));

        CommentDto result = commentService.update(1L, "Updated comment text").block();

        assertThat(result).isEqualTo(commentDto);
        verify(commentRepository).findById(1L);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void delete() {
        when(commentRepository.deleteById(1L)).thenReturn(Mono.empty());

        commentService.deleteById(1L).block();

        verify(commentRepository).deleteById(1L);
    }
}
