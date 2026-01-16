package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("Методы сервиса должны ")
@SpringBootTest(classes = {CsvQuestionDao.class, LocalizedIOServiceImpl.class})
class TestServiceImplTest {

    @MockitoBean
    private QuestionDao questionDao;

    @MockitoBean
    private LocalizedIOService ioService;

    private static final List<Answer> stubAnswers = List.of(
            new Answer("Certainly. The red UFO is from Mars. And green is from Venus", false),
            new Answer("Absolutely not", false),
            new Answer("Science doesn't know this yet", true)
    );

    private static final Student student = new Student("John", "Doe");

    private static final Question stubQuestion = new Question("Is there life on Mars?", stubAnswers);

    private static final List<Question> stubQuestions = List.of(stubQuestion);

    @BeforeEach
    void setUp() {
        given(questionDao.findAll()).willReturn(stubQuestions);
    }

    @Test
    @DisplayName(" вызывать методы ioService и questionDao с нужными параметрами в нужном порядке и ничего более. (executeTest)")
    void shouldPrintQuestionAndAnswersInOrder() {
        TestServiceImpl testService = new TestServiceImpl(ioService, questionDao);
        var expectedQuestionString = """
                1. Is there life on Mars?
                \t1) Certainly. The red UFO is from Mars. And green is from Venus
                \t2) Absolutely not
                \t3) Science doesn't know this yet""";
        String prompt = "prompt";
        String errorMessage = "errorMessage";
        given(ioService.getMessage("TestService.write.number.from.to", 1, 3)).willReturn(prompt);
        given(ioService.getMessage("TestService.wrong.input")).willReturn(errorMessage);
        given(ioService.readIntForRangeWithPrompt(anyInt(), anyInt(), anyString(), anyString())).willReturn(1);

        testService.executeTestFor(student);

        verify(questionDao).findAll();
        verifyNoMoreInteractions(questionDao);

        InOrder inOrder = inOrder(ioService);
        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printLineLocalized("TestService.answer.the.questions");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        inOrder.verify(ioService).printLine(captor.capture());
        assertEquals(
                expectedQuestionString.replace("\r\n", "\n"),
                captor.getValue().replace("\r\n", "\n")
        );

        inOrder.verify(ioService).readIntForRangeWithPrompt(1, 3, prompt, errorMessage);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("executeTest обрабатывает все вопросы и все ответы")
    void shouldProcessAllQuestionsAndAnswers() {
        Question question1 = new Question("Question 1", List.of(
                new Answer("Answer 1", true),
                new Answer("Answer 2", false)
        ));
        Question question2 = new Question("Question 2", List.of(
                new Answer("Answer 3", true)
        ));

        given(questionDao.findAll()).willReturn(List.of(question1, question2));
        given(ioService.getMessage(anyString(), any(), any())).willReturn("");
        given(ioService.getMessage(anyString())).willReturn("");
        given(ioService.readIntForRangeWithPrompt(anyInt(), anyInt(), anyString(), anyString())).willReturn(1);

        TestServiceImpl testService = new TestServiceImpl(ioService, questionDao);

        testService.executeTestFor(student);

        verify(ioService).printLine(contains("Question 1"));
        verify(ioService).printLine(contains("Question 2"));

        verify(ioService).printLine(contains("Answer 1"));
        verify(ioService).printLine(contains("Answer 2"));
        verify(ioService).printLine(contains("Answer 3"));
    }
}