package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.exceptions.InvalidQuestionFormatException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Сервис TestServiceImpl должен")
class TestServiceImplTestLLM {

    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private TestServiceImpl testService;

    private Student testStudent;
    private Question validQuestion;
    private Question invalidQuestion;
    private Question emptyQuestion;

    @BeforeEach
    void setUp() {
        testStudent = new Student("Иван", "Иванов");


        validQuestion = new Question(
                "Сколько будет 2+2?",
                List.of(
                        new Answer("3", false),
                        new Answer("4", true),
                        new Answer("5", false)
                )
        );

        invalidQuestion = new Question(
                "Вопрос без правильного ответа",
                List.of(
                        new Answer("Ответ 1", false),
                        new Answer("Ответ 2", false)
                )
        );

        emptyQuestion = new Question(
                "Вопрос без ответов",
                List.of()
        );
    }

    @Test
    @DisplayName("корректно выполнять тест с валидными вопросами")
    void shouldExecuteTestSuccessfullyWithValidQuestions() {
        // Подготовка
        List<Question> questions = List.of(validQuestion);
        when(questionDao.findAll()).thenReturn(questions);

        // Для первого вопроса пользователь выбирает правильный ответ (2)
        when(ioService.readIntForRangeWithPrompt(
                eq(1), eq(3), anyString(), anyString()
        )).thenReturn(2);

        // Выполнение
        TestResult result = testService.executeTestFor(testStudent);

        // Проверка
        assertThat(result).isNotNull();
        assertThat(result.getStudent()).isEqualTo(testStudent);
        assertThat(result.getAnsweredQuestions()).hasSize(1);
        assertThat(result.getRightAnswersCount()).isEqualTo(1);

        // Проверка вызовов IOService
        verify(ioService).printLine("");
        verify(ioService).printFormattedLine("Please answer the questions below%n");
        verify(ioService).printLine("1. Сколько будет 2+2?\n\t1) 3\n\t2) 4\n\t3) 5\n");
        verify(ioService).readIntForRangeWithPrompt(1, 3, "Write number from 1 to 3", "Wrong input!!!");
    }

    @Test
    @DisplayName("корректно выполнять тест с неправильными ответами")
    void shouldExecuteTestWithIncorrectAnswers() {
        // Подготовка
        List<Question> questions = List.of(validQuestion);
        when(questionDao.findAll()).thenReturn(questions);

        // Пользователь выбирает неправильный ответ (1)
        when(ioService.readIntForRangeWithPrompt(
                eq(1), eq(3), anyString(), anyString()
        )).thenReturn(1);

        // Выполнение
        TestResult result = testService.executeTestFor(testStudent);

        // Проверка
        assertThat(result).isNotNull();
        assertThat(result.getRightAnswersCount()).isEqualTo(0);
        assertThat(result.getAnsweredQuestions()).hasSize(1);
    }

    @Test
    @DisplayName("обрабатывать множественные вопросы")
    void shouldHandleMultipleQuestions() {
        // Подготовка
        List<Question> questions = List.of(validQuestion, validQuestion);
        when(questionDao.findAll()).thenReturn(questions);

        // Первый вопрос - правильный ответ, второй - неправильный
        when(ioService.readIntForRangeWithPrompt(
                eq(1), eq(3), anyString(), anyString()
        )).thenReturn(2, 1);

        // Выполнение
        TestResult result = testService.executeTestFor(testStudent);

        // Проверка
        assertThat(result).isNotNull();
        assertThat(result.getRightAnswersCount()).isEqualTo(1);
        assertThat(result.getAnsweredQuestions()).hasSize(2);
    }

    @Test
    @DisplayName("бросать исключение при вопросе без правильного ответа")
    void shouldThrowExceptionForQuestionWithoutCorrectAnswer() {
        // Подготовка
        List<Question> questions = List.of(invalidQuestion);
        when(questionDao.findAll()).thenReturn(questions);

        // Выполнение и проверка
        assertThatThrownBy(() -> testService.executeTestFor(testStudent))
                .isInstanceOf(InvalidQuestionFormatException.class)
                .hasMessage("No correct answer in question.");
    }

    @Test
    @DisplayName("бросать исключение при пустом вопросе")
    void shouldThrowExceptionForEmptyQuestion() {
        // Подготовка
        List<Question> questions = List.of(emptyQuestion);
        when(questionDao.findAll()).thenReturn(questions);

        // Выполнение и проверка
        assertThatThrownBy(() -> testService.executeTestFor(testStudent))
                .isInstanceOf(InvalidQuestionFormatException.class)
                .hasMessage("No correct answer in question.");
    }

    @Test
    @DisplayName("обрабатывать пустой список вопросов")
    void shouldHandleEmptyQuestionList() {
        // Подготовка
        List<Question> questions = List.of();
        when(questionDao.findAll()).thenReturn(questions);

        // Выполнение
        TestResult result = testService.executeTestFor(testStudent);

        // Проверка
        assertThat(result).isNotNull();
        assertThat(result.getRightAnswersCount()).isEqualTo(0);
        assertThat(result.getAnsweredQuestions()).isEmpty();
    }

    @Test
    @DisplayName("корректно форматировать вопросы и ответы")
    void shouldFormatQuestionsAndAnswersCorrectly() {
        // Подготовка
        List<Question> questions = List.of(validQuestion);
        when(questionDao.findAll()).thenReturn(questions);

        when(ioService.readIntForRangeWithPrompt(
                eq(1), eq(3), anyString(), anyString()
        )).thenReturn(1);

        // Выполнение
        testService.executeTestFor(testStudent);

        // Проверка формата вывода вопроса
        ArgumentCaptor<String> questionCaptor = ArgumentCaptor.forClass(String.class);
        verify(ioService, times(2)).printLine(questionCaptor.capture());

        List<String> printedQuestion = questionCaptor.getAllValues();
        assertThat(printedQuestion).contains("1. Сколько будет 2+2?");
        assertThat(printedQuestion).contains("\t1) 3");
        assertThat(printedQuestion).contains("\t2) 4");
        assertThat(printedQuestion).contains("\t3) 5");
    }
}