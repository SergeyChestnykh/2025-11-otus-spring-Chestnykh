package ru.otus.hw.security;

import org.junit.jupiter.params.provider.Arguments;
import org.springframework.lang.Nullable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class ControllerSecurityTest {

    private static final String TEST_USER = "user";
    private static final String LOGIN_REDIRECT_URL = "http://localhost/login";

    protected abstract String getSuccessRedirectUrl();

    public void executeRequestAndVerify(MockMvc mockMvc,
                                        MockHttpServletRequestBuilder requestBuilder,
                                        @Nullable String userName,
                                        int expectedStatus,
                                        @Nullable String expectedRedirectUrl) throws Exception {
        if (userName != null) {
            requestBuilder.with(user(userName));
        }

        ResultActions resultActions = mockMvc.perform(requestBuilder)
                .andExpect(status().is(expectedStatus));

        if (expectedRedirectUrl != null) {
            resultActions.andExpect(redirectedUrl(expectedRedirectUrl));
        }
    }

    public Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of(TEST_USER, 302, getSuccessRedirectUrl()),
                Arguments.of(null, 302, LOGIN_REDIRECT_URL)
        );
    }
}
