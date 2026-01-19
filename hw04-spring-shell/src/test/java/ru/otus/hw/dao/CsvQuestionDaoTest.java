package ru.otus.hw.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@DisplayName("Методы dao должны ")
@SpringBootTest(classes = CsvQuestionDao.class)
class CsvQuestionDaoTest {

    @MockitoBean
    private TestFileNameProvider testFileNameProvider;

    @Autowired
    private QuestionDao dao;

    private static final List<Question> testQuestions = List.of(
            new Question("Question 1?", List.of(
                    new Answer("Answer 1", true),
                    new Answer("Answer 2", false),
                    new Answer("Answer 3", false)
            )),
            new Question("Question 2?", List.of(
                    new Answer("Yes", false),
                    new Answer("No", true)
            )),
            new Question("Question 3?", List.of(
                    new Answer("Option A", false),
                    new Answer("Option B", false),
                    new Answer("Option C", true)
            ))
    );

    @Test
    @DisplayName(" возвращать все вопросы из файла в заданном порядке")
    void shouldReturnAllQuestionsFromFile() {

        when(testFileNameProvider.getTestFileName()).thenReturn("questions.csv");

        List<Question> questions = dao.findAll();

        assertEquals(3, questions.size());
        assertEquals(testQuestions.get(0), questions.get(0));
        assertEquals(testQuestions.get(1), questions.get(1));
        assertEquals(testQuestions.get(2), questions.get(2));
    }


    @Test
    @DisplayName(" выбрасывать QuestionReadException при IOException")
    void shouldThrowExceptionOnIOException() {
        given(testFileNameProvider.getTestFileName()).willReturn("wrong_filename.csv");

        RuntimeException exception = assertThrows(RuntimeException.class, dao::findAll);

        assertInstanceOf(QuestionReadException.class, exception);
    }
}
