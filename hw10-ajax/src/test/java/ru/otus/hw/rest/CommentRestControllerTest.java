package ru.otus.hw.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentRestController.class)
class CommentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @Test
    void getAllCommentsForBook_ReturnsCommentsList() throws Exception {
        CommentDto comment1 = new CommentDto(1L, "Comment One");
        CommentDto comment2 = new CommentDto(2L, "Comment Two");

        when(commentService.findAllForBook(1L)).thenReturn(List.of(comment1, comment2));

        mockMvc.perform(get("/api/book/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].text").value("Comment One"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].text").value("Comment Two"));

        verify(commentService).findAllForBook(1L);
    }

    @Test
    void getAllCommentsForBook_ReturnsEmptyList() throws Exception {
        when(commentService.findAllForBook(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/book/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(commentService).findAllForBook(1L);
    }

    @Test
    void deleteComment_DeletesSuccessfully() throws Exception {
        doNothing().when(commentService).deleteById(1L);

        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isOk());

        verify(commentService).deleteById(1L);
    }

    @Test
    void addComment_CreatesSuccessfully() throws Exception {
        CommentDto commentDto = new CommentDto(null, "New Comment");
        String requestBody = objectMapper.writeValueAsString(commentDto);

        when(commentService.insert(eq(1L), eq("New Comment"))).thenReturn(commentDto);

        mockMvc.perform(post("/api/book/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(commentService).insert(eq(1L), eq("New Comment"));
    }

    @Test
    void addComment_WithValidText_CreatesSuccessfully() throws Exception {
        String requestBody = """
            {
                "text": "This is a valid comment"
            }
            """;

        CommentDto commentDto = new CommentDto(1L, "This is a valid comment");
        when(commentService.insert(eq(1L), eq("This is a valid comment"))).thenReturn(commentDto);

        mockMvc.perform(post("/api/book/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(commentService).insert(eq(1L), eq("This is a valid comment"));
    }
}
