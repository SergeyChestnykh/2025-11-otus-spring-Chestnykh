package ru.otus.hw.rest;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

@RestController
@AllArgsConstructor
public class AuthorRestController {

    private final AuthorService authorService;

    @GetMapping("/api/authors")
    List<AuthorDto> getAllAuthors() {
        return authorService.findAll();
    }

}
