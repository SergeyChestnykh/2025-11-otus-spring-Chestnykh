package ru.otus.hw.rest;

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
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class BookRestController {
    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;

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

    @GetMapping("/api/authors")
    List<AuthorDto> getAllAuthors() {
        return authorService.findAll();
    }

    @GetMapping("/api/genres")
    List<GenreDto> getAllGenres() {
        return genreService.findAll();
    }

    @PostMapping("/api/book")
    ResponseEntity<BookDto> createBook(@RequestBody Map<String, Object> bookData) {
        String title = (String) bookData.get("title");
        long authorId = ((Number) bookData.get("authorId")).longValue();
        List<Long> genreIds = ((List<Number>) bookData.get("genreIds")).stream()
                .map(Number::longValue)
                .toList();
        
        bookService.insert(title, authorId, new HashSet<>(genreIds));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/api/book/{id}")
    ResponseEntity<BookDto> updateBook(@PathVariable long id, @RequestBody Map<String, Object> bookData) {
        String title = (String) bookData.get("title");
        Long authorId = ((Number) bookData.get("authorId")).longValue();
        List<Long> genreIds = ((List<Number>) bookData.get("genreIds")).stream()
                .map(Number::longValue)
                .toList();
        
        bookService.update(id, title, authorId, new HashSet<>(genreIds));
        return ResponseEntity.ok().build();
    }
}
