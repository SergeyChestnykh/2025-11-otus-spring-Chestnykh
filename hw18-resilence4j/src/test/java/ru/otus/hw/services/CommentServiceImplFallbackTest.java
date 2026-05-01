package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.InsertEntityException;
import ru.otus.hw.repositories.CommentRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class CommentServiceImplFallbackTest {
    @Autowired
    private CommentService commentService;

    @MockitoBean
    private CommentRepository commentRepository;

    @Test
    void insert_fallback() {
        when(commentRepository.save(any())).thenThrow(new RuntimeException());

        assertThrows(
                InsertEntityException.class,
                () -> commentService.insert(1L, "Test comment text")
        );
    }
}