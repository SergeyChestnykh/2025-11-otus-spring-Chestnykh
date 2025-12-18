package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.exceptions.InvalidQuestionFormatException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private static final String ERROR_PROMPT = "Wrong input!!!";

    private static final String ANSWER_PROMPT_FORMAT = "Write number from %d to %d";

    private static final String QUESTION_FORMAT = "%d. %s";

    private static final String ANSWER_FORMAT = "\t%d) %s";

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        runTest(questions, testResult::applyAnswer);

        return testResult;
    }

    private void runTest(List<Question> questions, BiConsumer<Question, Boolean> applyAnswer) {
        int questionNumber = 1;
        for (var question : questions) {
            var testQuestion = questionToTestQuestion(question, questionNumber++);

            printQuestion(testQuestion);

            int userAnswerNumber = getUserAnswerNumber(testQuestion);

            var isAnswerValid = userAnswerNumber == testQuestion.correctAnswerNumber;
            applyAnswer.accept(question, isAnswerValid);
        }
    }

    private void printQuestion(TestQuestion testQuestion) {
        ioService.printFormattedLine(QUESTION_FORMAT, testQuestion.questionNumber, testQuestion.questionText);
        testQuestion.orderedAnswers.forEach((answerNumber, answerText) ->
                ioService.printFormattedLine(ANSWER_FORMAT, answerNumber, answerText)
        );
        ioService.printLine("");
    }

    private int getUserAnswerNumber(TestQuestion testQuestion) {
        var answerPrompt = String.format(
                ANSWER_PROMPT_FORMAT,
                testQuestion.minAnswerNumber,
                testQuestion.maxAnswerNumber
        );
        return ioService.readIntForRangeWithPrompt(
                testQuestion.minAnswerNumber,
                testQuestion.maxAnswerNumber,
                answerPrompt,
                ERROR_PROMPT
        );
    }

    private TestQuestion questionToTestQuestion(Question question, int questionNumber) {
        final int minAnswerNumber = 1;
        final int maxAnswerNumber = question.answers().size();

        var orderedAnswers = new LinkedHashMap<Integer, String>();
        int correctAnswerNumber = -1;
        int answerNumber = minAnswerNumber;
        for (Answer answer : question.answers()) {
            if (answer.isCorrect()) {
                correctAnswerNumber = answerNumber;
            }
            orderedAnswers.put(answerNumber++, answer.text());
        }

        if (correctAnswerNumber == -1) {
            throw new InvalidQuestionFormatException("No correct answer in question.");
        }

        return new TestQuestion(
                question.text(), questionNumber, orderedAnswers, minAnswerNumber, maxAnswerNumber, correctAnswerNumber
        );
    }

    private record TestQuestion(
            String questionText,
            int questionNumber,
            Map<Integer, String> orderedAnswers,
            int minAnswerNumber,
            int maxAnswerNumber,
            int correctAnswerNumber
    ) {
    }
}
