package ru.otus.hw.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorRestController.class)
class AuthorRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    @Test
    void getAllAuthors_ReturnsAuthorsList() throws Exception {
        AuthorDto author1 = new AuthorDto(1L, "Author One");
        AuthorDto author2 = new AuthorDto(2L, "Author Two");

        when(authorService.findAll()).thenReturn(List.of(author1, author2));

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].fullName").value("Author One"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].fullName").value("Author Two"));

        verify(authorService).findAll();
    }

    @Test
    void getAllAuthors_ReturnsEmptyList() throws Exception {
        when(authorService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(authorService).findAll();
    }

    @Test
    void getAllAuthors_ReturnsSingleAuthor() throws Exception {
        AuthorDto author = new AuthorDto(1L, "Only Author");

        when(authorService.findAll()).thenReturn(List.of(author));

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].fullName").value("Only Author"));

        verify(authorService).findAll();
    }
}
