package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private BookConverter bookConverter;

    @Test
    void listBooks_shouldReturnBooksList() throws Exception {
        List<BookDto> books = List.of(
                new BookDto(
                        1L,
                        "Book 1",
                        new AuthorDto(1L, "Author 1"),
                        List.of(new GenreDto(1L, "Genre 1")))
        );
        when(bookService.findAll()).thenReturn(books);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("list"))
                .andExpect(model().attribute("bookList", books));
    }

    @Test
    void showBook_shouldReturnBookDetails() throws Exception {
        BookDto book = new BookDto(1L, "Test Book", new AuthorDto(1L, "Author 1"),
                List.of(new GenreDto(1L, "Genre 1")));
        when(bookService.findById(1L)).thenReturn(book);

        List<CommentDto> comments = List.of(new CommentDto(1L, "Comment text"));
        when(commentService.findAllForBook(1L)).thenReturn(comments);

        mockMvc.perform(get("/book/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("detail"))
                .andExpect(model().attribute("book", book))
                .andExpect(model().attribute("comments", comments));
    }

    @Test
    void newBookForm_shouldReturnNewBookForm() throws Exception {
        when(authorService.findAll()).thenReturn(List.of(new AuthorDto(1L, "Author 1")));
        when(genreService.findAll()).thenReturn(List.of(new GenreDto(1L, "Genre 1")));

        mockMvc.perform(get("/book/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"))
                .andExpect(model().attribute("book", new BookFormDto()))
                .andExpect(model().attribute("isNew", true));
    }

    @Test
    void editBookForm_shouldReturnEditBookForm() throws Exception {
        BookDto book = new BookDto(1L, "Test Book", new AuthorDto(1L, "Author 1"),
                List.of(new GenreDto(1L, "Genre 1")));
        BookFormDto bookFormDto = new BookFormDto(1L, "Test Book", 1L, List.of(1L));

        when(bookService.findById(1L)).thenReturn(book);
        when(bookConverter.toFormDto(book)).thenReturn(bookFormDto);

        mockMvc.perform(get("/book/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"))
                .andExpect(model().attribute("book", bookFormDto))
                .andExpect(model().attribute("isNew", false));
    }

    @Test
    void createBook_shouldRedirectToList() throws Exception {

        mockMvc.perform(post("/book")
                        .param("title", "New Book")
                        .param("authorId", "1")
                        .param("genreIds", "1", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService).insert(eq("New Book"), eq(1L), eq(java.util.Set.of(1L, 2L)));
    }

    @Test
    void createBook_shouldReturnFormOnValidationError() throws Exception {

        mockMvc.perform(post("/book")
                        .param("title", "")
                        .param("authorId", "1")
                        .param("genreIds", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"))
                .andExpect(model().attribute("isNew", true));
    }

    @Test
    void updateBook_shouldRedirectToList() throws Exception {

        mockMvc.perform(put("/book/1")
                        .param("id", "1")
                        .param("title", "Updated Book")
                        .param("authorId", "1")
                        .param("genreIds", "1", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService).update(eq(1L), eq("Updated Book"), eq(1L), eq(java.util.Set.of(1L, 2L)));
    }

    @Test
    void updateBook_shouldReturnFormOnValidationError() throws Exception {
        mockMvc.perform(put("/book/1")
                        .param("id", "1")
                        .param("title", "")
                        .param("authorId", "1")
                        .param("genreIds", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"))
                .andExpect(model().attribute("isNew", false));
    }

    @Test
    void deleteBook_shouldRedirectToList() throws Exception {
        doNothing().when(bookService).deleteById(1L);

        mockMvc.perform(delete("/book/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService).deleteById(1L);
    }
}
