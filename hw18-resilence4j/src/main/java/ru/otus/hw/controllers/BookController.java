package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class BookController {

    @GetMapping("/")
    public String listBooks() {
        return "list";
    }

    @GetMapping("/book/{id}")
    public String showBook(@PathVariable long id) {
        return "detail";
    }

    @GetMapping("/book/new")
    public String newBookForm() {
        return "edit";
    }

    @GetMapping("/book/{id}/edit")
    public String editBookForm(@PathVariable String id) {
        return "edit";
    }
}
