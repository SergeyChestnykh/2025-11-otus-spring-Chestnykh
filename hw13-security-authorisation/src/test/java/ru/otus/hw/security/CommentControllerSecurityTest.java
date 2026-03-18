package ru.otus.hw.security;

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
import ru.otus.hw.controllers.CommentController;
import ru.otus.hw.services.CommentService;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Тест безопасности CommentController")
@Import(SecurityConfiguration.class)
@WebMvcTest(controllers = CommentController.class)
public class CommentControllerSecurityTest extends ControllerSecurityTest {

    public static final int BOOK_ID = 1;

    @MockitoBean
    private CommentService commentService;

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("проверяющий добавление комментария")
    @ParameterizedTest(name = "Пользователь: {0} - статус: {1}")
    @MethodSource("getTestData")
    public void securityTestAddComment(
            @Nullable SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor principal,
            int expectedStatus,
            @Nullable String expectedRedirectUrl
    ) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/book/{bookId}/comment", BOOK_ID)
                .param("text", "new comment content")
                .with(csrf());

        performAndAssert(mockMvc, requestBuilder, principal, expectedStatus, expectedRedirectUrl);
    }

    @DisplayName("проверяющий обновление комментария")
    @ParameterizedTest(name = "Пользователь: {0} - статус: {1}")
    @MethodSource("getTestData")
    public void securityTestUpdateComment(
            @Nullable SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor principal,
            int expectedStatus,
            @Nullable String expectedRedirectUrl
    ) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/book/{bookId}/comment/1", BOOK_ID)
                .param("text", "update comment content")
                .with(csrf());

        performAndAssert(mockMvc, requestBuilder, principal, expectedStatus, expectedRedirectUrl);
    }

    @DisplayName("проверяющий удаление комментария")
    @ParameterizedTest(name = "Пользователь: {0} - статус: {1}")
    @MethodSource("getTestData")
    public void securityTestDeleteComment(
            @Nullable SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor principal,
            int expectedStatus,
            @Nullable String expectedRedirectUrl
    ) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/book/{bookId}/comment/1", BOOK_ID)
                .with(csrf());

        performAndAssert(mockMvc, requestBuilder, principal, expectedStatus, expectedRedirectUrl);
    }

    private static Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of(null, 302, LOGIN_REDIRECT_URL),
                Arguments.of(asUser(), 302, "/book/1"),
                Arguments.of(asAdmin(), 302, "/book/1")
        );
    }
}
