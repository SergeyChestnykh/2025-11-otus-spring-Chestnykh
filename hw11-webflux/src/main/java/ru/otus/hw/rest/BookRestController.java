package ru.otus.hw.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.services.BookService;

import java.util.HashSet;

@RequiredArgsConstructor
@RestController
public class BookRestController {

    private final BookService bookService;

    @GetMapping("/api/book")
    Flux<BookDto> getAll() {
        return bookService.findAll();
    }

    @GetMapping("/api/book/{id}")
    Mono<BookDto> getById(@PathVariable long id) {
        return bookService.findById(id);
    }

    @DeleteMapping("/api/book/{id}")
    Mono<Void> deleteById(@PathVariable long id) {
        return bookService.deleteById(id);
    }

    @PostMapping("/api/book")
    public Mono<ResponseEntity<BookDto>> createBook(@RequestBody @Valid BookFormDto bookDto) {
        return bookService.insert(
                bookDto.getTitle(),
                bookDto.getAuthorId(),
                new HashSet<>(bookDto.getGenreIds())
        ).map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved));
    }

    @PutMapping("/api/book/{id}")
    Mono<ResponseEntity<BookDto>> updateBook(@PathVariable long id, @RequestBody @Valid BookFormDto bookDto) {
        return bookService.update(
                        id,
                        bookDto.getTitle(),
                        bookDto.getAuthorId(),
                        new HashSet<>(bookDto.getGenreIds())
                ).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
