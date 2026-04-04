package ru.otus.hw.security;

import org.springframework.lang.Nullable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class ControllerSecurityTest {

    static final String TEST_USER = "user";
    static final String LOGIN_REDIRECT_URL = "http://localhost/login";

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
}
