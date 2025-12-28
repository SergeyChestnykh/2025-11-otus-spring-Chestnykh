package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.exceptions.InvalidQuestionFormatException;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private static final int INITIAL_ANSWER_NUMBER = 1;

    private final LocalizedIOService localizedIOService;

    private final QuestionDao questionDao;

    private final LocalizedMessagesServiceImpl localizedMessagesService;

    @Override
    public TestResult executeTestFor(Student student) {
        localizedIOService.printLine("");
        localizedIOService.printLineLocalized("TestService.answer.the.questions");
        localizedIOService.printLine("");

        var questions = questionDao.findAll();

        return runTest(questions, student);
    }

    private TestResult runTest(List<Question> questions, Student student) {
        TestResult testResult = new TestResult(student);
        int questionNumber = 1;
        for (var question : questions) {
            String questionString = convertQuestionToString(question, questionNumber++);

            localizedIOService.printLine(questionString);

            int userAnswerNumber = getUserAnswerNumber(question);

            var isAnswerValid = userAnswerNumber == getCorrectAnswerNumber(question);
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private int getUserAnswerNumber(Question question) {
        var answerPrompt = localizedMessagesService.getMessage(
                "TestService.write.number.from.to",
                INITIAL_ANSWER_NUMBER,
                getMaxAnswerNumber(question)
        );

        return localizedIOService.readIntForRangeWithPrompt(
                INITIAL_ANSWER_NUMBER,
                getMaxAnswerNumber(question),
                answerPrompt,
                localizedMessagesService.getMessage("TestService.wrong.input")
        );
    }

    private String convertQuestionToString(Question question, int questionNumber) {
        return getFormattedQuestion(question, questionNumber) + "\n" + getFormattedAnswers(question);
    }

    private String getFormattedQuestion(Question question, int questionNumber) {
        return localizedMessagesService.getMessage(
                "TestService.question.format",
                questionNumber,
                question.text()
        );
    }

    private String getFormattedAnswers(Question question) {
        StringBuilder stringBuilder = new StringBuilder();
        AtomicInteger answerNumber = new AtomicInteger(INITIAL_ANSWER_NUMBER);
        question.answers().stream()
                .map(answer ->
                        localizedMessagesService.getMessage(
                                "TestService.answer.format",
                                answerNumber.getAndIncrement(),
                                answer.text()
                        )
                )
                .forEach(formattedAnswer -> {
                            stringBuilder.append("\t");
                            stringBuilder.append(formattedAnswer);
                            stringBuilder.append("\n");
                        }
                );
        return stringBuilder.toString();
    }

    private int getCorrectAnswerNumber(Question question) {
        int correctAnswerNumber = -1;
        int answerNumber = INITIAL_ANSWER_NUMBER;
        for (Answer answer : question.answers()) {
            if (answer.isCorrect()) {
                correctAnswerNumber = answerNumber;
                break;
            }
            answerNumber++;
        }

        if (correctAnswerNumber == -1) {
            throw new InvalidQuestionFormatException("No correct answer in question.");
        }

        return answerNumber;
    }

    private int getMaxAnswerNumber(Question question) {
        return question.answers().size() - 1 + INITIAL_ANSWER_NUMBER;
    }
}
