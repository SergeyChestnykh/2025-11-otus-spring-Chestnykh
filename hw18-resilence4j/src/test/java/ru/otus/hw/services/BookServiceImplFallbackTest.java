package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.InsertEntityException;
import ru.otus.hw.repositories.BookRepository;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class BookServiceImplFallbackTest {
    @Autowired
    private BookService bookService;

    @MockitoBean
    private BookRepository bookRepository;

    @Test
    void insert_fallback() {
        when(bookRepository.save(any())).thenThrow(new RuntimeException());

        assertThrows(
                InsertEntityException.class,
                () -> bookService.insert("title", 1L, Set.of(1L))
        );
    }
}