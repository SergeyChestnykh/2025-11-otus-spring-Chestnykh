package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.HashSet;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {

    private static final String REDIRECT_ROOT = "redirect:/";

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

    @PostMapping("/book")
    public String createBook(@Valid @ModelAttribute(MODEL_ATTR_BOOK) BookFormDto createDto,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(MODEL_ATTR_NEW, true);
            return EDIT_FORM;
        }
        bookService.insert(createDto.getTitle(), createDto.getAuthorId(), new HashSet<>(createDto.getGenreIds()));
        return REDIRECT_ROOT;
    }

    @PutMapping("/book/{id}")
    public String updateBook(@PathVariable long id,
                             @Valid @ModelAttribute(MODEL_ATTR_BOOK) BookFormDto updateDto,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(MODEL_ATTR_NEW, false);
            return EDIT_FORM;
        }

        bookService.update(id, updateDto.getTitle(),
                updateDto.getAuthorId(), new HashSet<>(updateDto.getGenreIds()));
        return REDIRECT_ROOT;
    }

    @DeleteMapping("/book/{id}")
    public String deleteBook(@PathVariable long id) {
        bookService.deleteById(id);
        return REDIRECT_ROOT;
    }
}
