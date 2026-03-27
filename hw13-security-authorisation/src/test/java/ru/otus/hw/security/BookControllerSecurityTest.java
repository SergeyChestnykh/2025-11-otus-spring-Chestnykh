package ru.otus.hw.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.lang.Nullable;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.hw.controllers.BookController;
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
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Тест безопасности BookController")
@Import(SecurityConfiguration.class)
@WebMvcTest(controllers = BookController.class)
public class BookControllerSecurityTest extends ControllerSecurityTest {

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

    private static final Long TEST_BOOK_ID = 1L;
    private static final Long TEST_AUTHOR_ID = 1L;
    private static final Long TEST_GENRE_ID = 1L;
    private static final String TEST_BOOK_TITLE = "BookTitle_1";
    private static final String TEST_AUTHOR_NAME = "Author_1";
    private static final String TEST_GENRE_NAME = "Genre_1";

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

    @DisplayName("проверяющий получение списка книг")
    @ParameterizedTest(name = "Пользователь: {0} - статус: {1}")
    @MethodSource("getUserTestData")
    public void securityTestListBooks(
            @Nullable SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor principal,
            int expectedStatus,
            @Nullable String expectedRedirectUrl
    ) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/")
                .with(csrf());

        performAndAssert(mockMvc, requestBuilder, principal, expectedStatus, expectedRedirectUrl);
    }

    @DisplayName("проверяющий получение информации о книге")
    @ParameterizedTest(name = "Пользователь: {0} - статус: {1}")
    @MethodSource("getUserTestData")
    public void securityTestBookInfo(
            @Nullable SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor principal,
            int expectedStatus,
            @Nullable String expectedRedirectUrl
    ) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/book/1")
                .with(csrf());

        performAndAssert(mockMvc, requestBuilder, principal, expectedStatus, expectedRedirectUrl);
    }

    @DisplayName("проверяющий получение доступа к форме создания книги")
    @ParameterizedTest(name = "Пользователь: {0} - статус: {1}")
    @MethodSource("getAdminTestData")
    public void securityTestNewBookForm(
            @Nullable SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor principal,
            int expectedStatus,
            @Nullable String expectedRedirectUrl
    ) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/book/new")
                .with(csrf());

        performAndAssert(mockMvc, requestBuilder, principal, expectedStatus, expectedRedirectUrl);
    }

    @DisplayName("проверяющий получение доступа к форме изменения книги")
    @ParameterizedTest(name = "Пользователь: {0} - статус: {1}")
    @MethodSource("getAdminTestData")
    public void securityTestEditBookForm(
            @Nullable SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor principal,
            int expectedStatus,
            @Nullable String expectedRedirectUrl
    ) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/book/{bookId}/edit", TEST_BOOK_ID)
                .with(csrf());

        performAndAssert(mockMvc, requestBuilder, principal, expectedStatus, expectedRedirectUrl);
    }


    @DisplayName("проверяющий добавление книги")
    @ParameterizedTest(name = "Пользователь: {0} - статус: {1}")
    @MethodSource("getModifyingTestData")
    public void securityTestAddBook(
            @Nullable SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor principal,
            int expectedStatus,
            @Nullable String expectedRedirectUrl
    ) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/book")
                .param("title", "new book")
                .param("authorId", "1")
                .param("genreIds", "1")
                .with(csrf());

        performAndAssert(mockMvc, requestBuilder, principal, expectedStatus, expectedRedirectUrl);
    }

    @DisplayName("проверяющий обновление книги")
    @ParameterizedTest(name = "Пользователь: {0} - статус: {1}")
    @MethodSource("getModifyingTestData")
    public void securityTestUpdateBook(
            @Nullable SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor principal,
            int expectedStatus,
            @Nullable String expectedRedirectUrl
    ) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/book/{bookId}", TEST_BOOK_ID)
                .param("title", "new book")
                .param("authorId", "1")
                .param("genreIds", "1")
                .with(csrf());

        performAndAssert(mockMvc, requestBuilder, principal, expectedStatus, expectedRedirectUrl);
    }

    @DisplayName("проверяющий удаление книги")
    @ParameterizedTest(name = "Пользователь: {0} - статус: {1}")
    @MethodSource("getModifyingTestData")
    public void securityTestDeleteBook(
            @Nullable SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor principal,
            int expectedStatus,
            @Nullable String expectedRedirectUrl
    ) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/book/{bookId}", TEST_BOOK_ID)
                .with(csrf());

        performAndAssert(mockMvc, requestBuilder, principal, expectedStatus, expectedRedirectUrl);
    }

    public Stream<Arguments> getUserTestData() {
        return Stream.of(
                Arguments.of(null, 302, LOGIN_REDIRECT_URL),
                Arguments.of(asUser(), 200, null),
                Arguments.of(asAdmin(), 200, null)
        );
    }

    public Stream<Arguments> getAdminTestData() {
        return Stream.of(
                Arguments.of(null, 302, LOGIN_REDIRECT_URL),
                Arguments.of(asUser(), 403, null),
                Arguments.of(asAdmin(), 200, null)
        );
    }

    public Stream<Arguments> getModifyingTestData() {
        return Stream.of(
                Arguments.of(null, 302, LOGIN_REDIRECT_URL),
                Arguments.of(asUser(), 403, null),
                Arguments.of(asAdmin(), 302, "/")
        );
    }
}
