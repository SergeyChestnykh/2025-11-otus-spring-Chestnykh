package ru.otus.hw.security;

import org.springframework.lang.Nullable;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class ControllerSecurityTest {

    static final String LOGIN_REDIRECT_URL = "http://localhost/login";

    protected void performAndAssert(MockMvc mockMvc,
                                    MockHttpServletRequestBuilder request,
                                    @Nullable SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor principal,
                                    int expectedStatus,
                                    @Nullable String expectedRedirectUrl) throws Exception {
        if (principal != null) {
            request.with(principal);
        }
        ResultActions ra = mockMvc.perform(request)
                .andExpect(status().is(expectedStatus));
        if (expectedRedirectUrl != null) {
            ra.andExpect(redirectedUrl(expectedRedirectUrl));
        }
    }

    protected static SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor asUser() {
        return user("user");
    }

    protected static SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor asAdmin() {
        return user("admin").roles("ADMIN");
    }

}
