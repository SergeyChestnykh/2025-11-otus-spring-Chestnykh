package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@DisplayName("Методы dao должны ")
@ExtendWith(MockitoExtension.class)
class CsvQuestionDaoTest {

    @Mock
    TestFileNameProvider testFileNameProvider;

    private static final ResourceProvider resourceProviderStub = new ResourceProvider() {
        @Override
        public Reader getResourceReader(String fileName) throws IOException {
            ClassPathResource resource = new ClassPathResource(fileName);
            InputStream is = resource.getInputStream();
            return new InputStreamReader(is, StandardCharsets.UTF_8);
        }
    };

    private static final ResourceProvider failingResourceProvider = new ResourceProvider() {
        @Override
        public Reader getResourceReader(String fileName) throws IOException {
            throw new IOException("Simulated IO error");
        }
    };

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

    @BeforeEach
    void setUp() {
        given(testFileNameProvider.getTestFileName()).willReturn("questions.csv");
    }

    @Test
    @DisplayName(" возвращать все вопросы из файла в заданном порядке")
    void shouldReturnAllQuestionsFromFile() {
        var dao = new CsvQuestionDao(testFileNameProvider, resourceProviderStub);

        List<Question> questions = dao.findAll();

        assertEquals(3, questions.size());
        assertEquals(testQuestions.get(0), questions.get(0));
        assertEquals(testQuestions.get(1), questions.get(1));
        assertEquals(testQuestions.get(2), questions.get(2));
    }


    @Test
    @DisplayName(" выбрасывать QuestionReadException при IOException")
    void shouldThrowExceptionOnIOException() {
        var dao = new CsvQuestionDao(testFileNameProvider, failingResourceProvider);

        RuntimeException exception = assertThrows(RuntimeException.class, dao::findAll);

        assertInstanceOf(QuestionReadException.class, exception);
        assertEquals("Simulated IO error", exception.getMessage());
    }
}














