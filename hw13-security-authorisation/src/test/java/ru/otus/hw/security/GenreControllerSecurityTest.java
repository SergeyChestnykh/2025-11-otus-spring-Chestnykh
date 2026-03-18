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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.hw.controllers.GenreController;
import ru.otus.hw.services.GenreService;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Тест безопасности GenreController")
@Import(SecurityConfiguration.class)
@WebMvcTest(controllers = GenreController.class)
public class GenreControllerSecurityTest extends ControllerSecurityTest {

    @MockitoBean
    private GenreService service;

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("проверяющий получение списка жанров")
    @ParameterizedTest(name = "Пользователь: {0} - статус: {1}")
    @MethodSource("getTestData")
    public void securityTestListGenres(
            @Nullable String userName,
            int expectedStatus,
            @Nullable String expectedRedirectUrl
    ) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/genres")
                .with(csrf());

        executeRequestAndVerify(mockMvc, requestBuilder, userName, expectedStatus, expectedRedirectUrl);
    }

    public Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of(TEST_USER, 200, null),
                Arguments.of(null, 302, LOGIN_REDIRECT_URL)
        );
    }
}