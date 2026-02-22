package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {

    private static final String MODEL_ATTR_BOOK = "book";

    private static final String MODEL_ATTR_NEW = "isNew";

    private static final String EDIT_FORM = "edit";

    private final AuthorService authorService;

    private final BookService bookService;

    private final GenreService genreService;

    private final CommentService commentService;

    private final BookConverter bookConverter;

    @ModelAttribute("authors")
    public List<AuthorDto> populateAuthors() {
        return authorService.findAll();
    }

    @ModelAttribute("genres")
    public List<GenreDto> populateGenres() {
        return genreService.findAll();
    }

    @GetMapping("/")
    public String listBooks(Model model) {
        model.addAttribute("bookList", bookService.findAll());
        return "list";
    }

    @GetMapping("/book/{id}")
    public String showBook(@PathVariable long id, Model model) {
        model.addAttribute(MODEL_ATTR_BOOK, bookService.findById(id));
        model.addAttribute("comments", commentService.findAllForBook(id));
        return "detail";
    }

    @GetMapping("/book/new")
    public String newBookForm(Model model) {
        model.addAttribute(MODEL_ATTR_BOOK, new BookFormDto());
        model.addAttribute(MODEL_ATTR_NEW, true);
        return EDIT_FORM;
    }

    @GetMapping("/book/{id}/edit")
    public String editBookForm(@PathVariable long id, Model model) {
        model.addAttribute(MODEL_ATTR_BOOK, bookConverter.toFormDto(bookService.findById(id)));
        model.addAttribute(MODEL_ATTR_NEW, false);
        return EDIT_FORM;
    }
}
