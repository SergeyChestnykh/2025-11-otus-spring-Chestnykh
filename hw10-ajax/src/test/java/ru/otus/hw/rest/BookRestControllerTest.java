package ru.otus.hw.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.BookService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookRestController.class)
class BookRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Test
    void getAllBooks_ReturnsBooksList() throws Exception {
        AuthorDto author = new AuthorDto(1L, "Author Name");
        GenreDto genre = new GenreDto(1L, "Genre Name");
        BookDto book = new BookDto(1L, "Book Title", author, List.of(genre));

        when(bookService.findAll()).thenReturn(List.of(book));

        mockMvc.perform(get("/api/book"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Book Title"))
                .andExpect(jsonPath("$[0].author.fullName").value("Author Name"))
                .andExpect(jsonPath("$[0].genres[0].name").value("Genre Name"));

        verify(bookService).findAll();
    }

    @Test
    void getAllBooks_ReturnsEmptyList() throws Exception {
        when(bookService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/book"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(bookService).findAll();
    }

    @Test
    void getBookById_ReturnsBook() throws Exception {
        AuthorDto author = new AuthorDto(1L, "Author Name");
        GenreDto genre = new GenreDto(1L, "Genre Name");
        BookDto book = new BookDto(1L, "Book Title", author, List.of(genre));

        when(bookService.findById(1L)).thenReturn(book);

        mockMvc.perform(get("/api/book/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Book Title"))
                .andExpect(jsonPath("$.author.fullName").value("Author Name"));

        verify(bookService).findById(1L);
    }

    @Test
    void deleteBook_DeletesSuccessfully() throws Exception {
        doNothing().when(bookService).deleteById(1L);

        mockMvc.perform(delete("/api/book/1"))
                .andExpect(status().isOk());

        verify(bookService).deleteById(1L);
    }

    @Test
    void createBook_CreatesSuccessfully() throws Exception {
        String requestBody = """
            {
                "title": "New Book",
                "authorId": 1,
                "genreIds": [1, 2]
            }
            """;

        AuthorDto author = new AuthorDto(1L, "Author Name");
        GenreDto genre = new GenreDto(1L, "Genre Name");
        BookDto createdBook = new BookDto(1L, "New Book", author, List.of(genre));

        when(bookService.insert(eq("New Book"), eq(1L), any())).thenReturn(createdBook);

        mockMvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        verify(bookService).insert(eq("New Book"), eq(1L), any());
    }

    @Test
    void updateBook_UpdatesSuccessfully() throws Exception {
        String requestBody = """
            {
                "title": "Updated Book",
                "authorId": 2,
                "genreIds": [1]
            }
            """;

        AuthorDto author = new AuthorDto(2L, "Updated Author");
        GenreDto genre = new GenreDto(1L, "Genre Name");
        BookDto updatedBook = new BookDto(1L, "Updated Book", author, List.of(genre));

        when(bookService.update(eq(1L), eq("Updated Book"), eq(2L), any())).thenReturn(updatedBook);

        mockMvc.perform(put("/api/book/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(bookService).update(eq(1L), eq("Updated Book"), eq(2L), any());
    }
}
