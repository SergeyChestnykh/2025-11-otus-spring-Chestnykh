package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.exceptions.InvalidQuestionFormatException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("Методы сервиса должны ")
@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {

    @Mock
    private QuestionDao questionDao;

    @Mock
    private IOService ioService;

    private static final String HEADER = "Please answer the questions below%n";

    private static final String QUESTION_FORMAT = "%d. %s";

    private static final String ANSWER_FORMAT = "\t%d) %s";

    public static final String ERROR_PROMPT = "Wrong input!!!";

    private static final String ANSWER_PROMPT_FORMAT = "Write number from %d to %d";

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

        testService.executeTestFor(student);

        verify(questionDao).findAll();
        verifyNoMoreInteractions(questionDao);

        InOrder inOrder = inOrder(ioService);
        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printFormattedLine(HEADER);
        inOrder.verify(ioService).printFormattedLine(QUESTION_FORMAT, 1, stubQuestion.text());
        inOrder.verify(ioService).printFormattedLine(ANSWER_FORMAT, 1, stubAnswers.get(0).text());
        inOrder.verify(ioService).printFormattedLine(ANSWER_FORMAT, 2, stubAnswers.get(1).text());
        inOrder.verify(ioService).printFormattedLine(ANSWER_FORMAT, 3, stubAnswers.get(2).text());
        inOrder.verify(ioService).printLine("");
        var answerPrompt = String.format(ANSWER_PROMPT_FORMAT, 1, 3);
        inOrder.verify(ioService).readIntForRangeWithPrompt(1, 3, answerPrompt, ERROR_PROMPT);
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

        TestServiceImpl testService = new TestServiceImpl(ioService, questionDao);

        testService.executeTestFor(student);

        verify(ioService).printFormattedLine(any(String.class), any(), eq("Question 1"));
        verify(ioService).printFormattedLine(any(String.class), any(), eq("Question 2"));

        verify(ioService).printFormattedLine(any(String.class), any(), eq("Answer 1"));
        verify(ioService).printFormattedLine(any(String.class), any(), eq("Answer 2"));
        verify(ioService).printFormattedLine(any(String.class), any(), eq("Answer 3"));
    }

    @Test
    @DisplayName("executeTest выбрасывает исключение при отсутствии правильных ответов")
    void shouldThrowExceptionWhenNoCorrectAnswer() {
        Question questionWithoutCorrectAnswer = new Question("Question without correct answer", List.of(
                new Answer("Answer 1", false),
                new Answer("Answer 2", false)
        ));

        given(questionDao.findAll()).willReturn(List.of(questionWithoutCorrectAnswer));

        TestServiceImpl testService = new TestServiceImpl(ioService, questionDao);

        assertThrows(InvalidQuestionFormatException.class, () -> testService.executeTestFor(student));
    }
}