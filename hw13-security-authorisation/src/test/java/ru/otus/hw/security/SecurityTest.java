package ru.otus.hw.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.lang.Nullable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.hw.controllers.AuthorController;
import ru.otus.hw.controllers.BookController;
import ru.otus.hw.controllers.CommentController;
import ru.otus.hw.controllers.GenreController;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Тест безопасности контроллеров")
@Import(SecurityConfiguration.class)
@WebMvcTest(controllers = {
        BookController.class,
        AuthorController.class,
        GenreController.class,
        CommentController.class
})
public class SecurityTest {

    static final String LOGIN_REDIRECT_URL = "http://localhost/login";

    private static final Long TEST_BOOK_ID = 1L;
    private static final Long TEST_AUTHOR_ID = 1L;
    private static final Long TEST_GENRE_ID = 1L;
    private static final String TEST_BOOK_TITLE = "BookTitle_1";
    private static final String TEST_AUTHOR_NAME = "Author_1";
    private static final String TEST_GENRE_NAME = "Genre_1";

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

    @Autowired
    private MockMvc mockMvc;

    private static BookDto createTestBook() {
        return new BookDto(
                TEST_BOOK_ID,
                TEST_BOOK_TITLE,
                new AuthorDto(TEST_AUTHOR_ID, TEST_AUTHOR_NAME),
                List.of(new GenreDto(TEST_GENRE_ID, TEST_GENRE_NAME))
        );
    }

    private static BookFormDto createTestBookUpdateDto() {
        return new BookFormDto(
                TEST_BOOK_ID,
                TEST_BOOK_TITLE,
                TEST_AUTHOR_ID,
                List.of(TEST_GENRE_ID)
        );
    }

    private static List<AuthorDto> createTestAuthors() {
        return List.of(new AuthorDto(TEST_AUTHOR_ID, TEST_AUTHOR_NAME));
    }

    private static List<GenreDto> createTestGenres() {
        return List.of(new GenreDto(TEST_GENRE_ID, TEST_GENRE_NAME));
    }

    @BeforeEach
    void setup() {
        when(bookService.findById(anyLong())).thenReturn(createTestBook());
        when(authorService.findAll()).thenReturn(createTestAuthors());
        when(genreService.findAll()).thenReturn(createTestGenres());
        when(bookConverter.toFormDto(any())).thenReturn(createTestBookUpdateDto());
    }

    @ParameterizedTest(name = "{0} {1} -> {2} ({3})")
    @CsvSource(delimiter = '|', textBlock = """
            # Эндпойнт | Метод | Пользователь | Ожидаемый статус | URL редиректа
            # Доступные эндпойнты (любой пользователь)
            /               | GET   | null        | 302              | http://localhost/login
            /               | GET   | user        | 200              | 
            /               | GET   | admin       | 200              |
            /book/1         | GET   | null        | 302              | http://localhost/login
            /book/1         | GET   | user        | 200              |
            /book/1         | GET   | admin       | 200              |
            /authors        | GET   | null        | 302              | http://localhost/login
            /authors        | GET   | user        | 200              |
            /authors        | GET   | admin       | 200              |
            /genres         | GET   | null        | 302              | http://localhost/login
            /genres         | GET   | user        | 200              |
            /genres         | GET   | admin       | 200              |
            
            # Только для админа (формы)
            /book/new       | GET   | null        | 302              | http://localhost/login
            /book/new       | GET   | user        | 403              |
            /book/new       | GET   | admin       | 200              |
            /book/1/edit    | GET   | null        | 302              | http://localhost/login
            /book/1/edit    | GET   | user        | 403              |
            /book/1/edit    | GET   | admin       | 200              |
            
            # Только для админа (модификация)
            /book           | POST  | null        | 302              | http://localhost/login
            /book           | POST  | user        | 403              |
            /book           | POST  | admin       | 302              | /
            /book/1         | PUT   | null        | 302              | http://localhost/login
            /book/1         | PUT   | user        | 403              |
            /book/1         | PUT   | admin       | 302              | /
            /book/1         | DELETE| null        | 302              | http://localhost/login
            /book/1         | DELETE| user        | 403              |
            /book/1         | DELETE| admin       | 302              | /
            
            # Комментарии (любой аутентифицированный пользователь)
            /book/1/comment | POST  | null        | 302              | http://localhost/login
            /book/1/comment | POST  | user        | 302              | /book/1
            /book/1/comment | POST  | admin       | 302              | /book/1
            /book/1/comment/1| PUT  | null        | 302              | http://localhost/login
            /book/1/comment/1| PUT  | user        | 302              | /book/1
            /book/1/comment/1| PUT  | admin       | 302              | /book/1
            /book/1/comment/1| DELETE| null       | 302              | http://localhost/login
            /book/1/comment/1| DELETE| user       | 302              | /book/1
            /book/1/comment/1| DELETE| admin       | 302              | /book/1
            """)
    public void testSecurityEndpoint(
            String endpoint,
            String httpMethod,
            String userRole,
            int expectedStatus,
            @Nullable String expectedRedirectUrl
    ) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = buildRequest(endpoint, httpMethod);

        // Применяем CSRF
        requestBuilder = requestBuilder.with(csrf());

        // Применяем пользователя только если роль указана
        if (userRole != null) {
            requestBuilder = applyUser(requestBuilder, userRole);
        }

        ResultActions resultActions = mockMvc.perform(requestBuilder)
                .andExpect(status().is(expectedStatus));

        if (expectedRedirectUrl != null && !expectedRedirectUrl.isEmpty()) {
            resultActions.andExpect(redirectedUrl(expectedRedirectUrl));
        }
    }

    private MockHttpServletRequestBuilder applyUser(MockHttpServletRequestBuilder requestBuilder, String userRole) {
        return switch (userRole) {
            case "user" -> requestBuilder.with(user("user"));
            case "admin" -> requestBuilder.with(user("admin").roles("ADMIN"));
            default -> requestBuilder;
        };
    }

    private MockHttpServletRequestBuilder buildRequest(String endpoint, String httpMethod) {
        return switch (httpMethod) {
            case "GET" -> MockMvcRequestBuilders.get(endpoint);
            case "POST" -> MockMvcRequestBuilders.post(endpoint)
                    .param("title", "new book")
                    .param("authorId", "1")
                    .param("genreIds", "1");
            case "PUT" -> MockMvcRequestBuilders.put(endpoint)
                    .param("title", "new book")
                    .param("authorId", "1")
                    .param("genreIds", "1");
            case "DELETE" -> MockMvcRequestBuilders.delete(endpoint);
            default -> throw new IllegalArgumentException("Unknown HTTP method: " + httpMethod);
        };
    }
}
