package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.domain.Student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@DisplayName("Методы сервиса должны ")
@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private IOService ioService;

    private static final String FIRST_NAME_PROMPT = "Please input your first name";
    private static final String LAST_NAME_PROMPT = "Please input your last name";
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";

    @BeforeEach
    void setUp() {
        given(ioService.readStringWithPrompt(FIRST_NAME_PROMPT)).willReturn(TEST_FIRST_NAME);
        given(ioService.readStringWithPrompt(LAST_NAME_PROMPT)).willReturn(TEST_LAST_NAME);
    }

    @Test
    @DisplayName(" вызывать методы ioService в правильном порядке и создавать корректного студента (determineCurrentStudent)")
    void shouldCallIOServiceMethodsInCorrectOrderAndCreateStudent() {
        StudentServiceImpl studentService = new StudentServiceImpl(ioService);

        Student result = studentService.determineCurrentStudent();

        assertEquals(TEST_FIRST_NAME, result.firstName());
        assertEquals(TEST_LAST_NAME, result.lastName());
        assertEquals("John Doe", result.getFullName());

        InOrder inOrder = inOrder(ioService);
        inOrder.verify(ioService).readStringWithPrompt(FIRST_NAME_PROMPT);
        inOrder.verify(ioService).readStringWithPrompt(LAST_NAME_PROMPT);
        inOrder.verifyNoMoreInteractions();

        verifyNoMoreInteractions(ioService);
    }
}