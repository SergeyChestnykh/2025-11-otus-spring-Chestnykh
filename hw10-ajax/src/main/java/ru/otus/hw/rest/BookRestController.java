package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.services.BookService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BookRestController {
    private final BookService bookService;

    @GetMapping("/api/book")
    List<BookDto> getAll() {
        return bookService.findAll();
    }

    @GetMapping("/api/book/{id}")
    BookDto getById(@PathVariable long id) {
        return bookService.findById(id);
    }

    @DeleteMapping("/api/book/{id}")
    void deleteById(@PathVariable long id) {
        bookService.deleteById(id);
    }
}
