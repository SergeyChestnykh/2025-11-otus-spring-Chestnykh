package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.exceptions.InvalidQuestionFormatException;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        int questionNumber = 1;
        for (var question : questions) {
            var testQuestion = questionToTestQuestion(question, questionNumber++);

            ioService.printLine(testQuestion.question);

            int userAnswerNumber = getUserAnswerNumber(testQuestion);

            var isAnswerValid = userAnswerNumber == testQuestion.correctAnswerNumber;
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private int getUserAnswerNumber(TestQuestion testQuestion) {
        var answerPrompt = String.format(
                "Write number from %s to %s",
                testQuestion.minAnswerNumber,
                testQuestion.maxAnswerNumber
        );
        var errorPrompt = "Wrong input!!!";
        return ioService.readIntForRangeWithPrompt(
                testQuestion.minAnswerNumber,
                testQuestion.maxAnswerNumber,
                answerPrompt,
                errorPrompt
        );
    }

    private TestQuestion questionToTestQuestion(Question question, int questionNumber) {
        final int minAnswerNumber = 1;
        final int maxAnswerNumber = question.answers().size();

        StringBuilder sb = new StringBuilder();
        sb.append(getFormattedQuestion(question.text(), questionNumber));
        int correctAnswerNumber = -1;
        int answerNumber = minAnswerNumber;
        for (Answer answer : question.answers()) {
            if (answer.isCorrect()) {
                correctAnswerNumber = answerNumber;
            }
            sb.append(getFormattedAnswer(answer.text(), answerNumber++));
        }

        if (correctAnswerNumber == -1) {
            throw new InvalidQuestionFormatException("No correct answer in question.");
        }

        return new TestQuestion(sb.toString(), minAnswerNumber, maxAnswerNumber, correctAnswerNumber);
    }

    private String getFormattedQuestion(String question, int questionNumber) {
        return String.format("%d. %s\n", questionNumber, question);
    }

    private String getFormattedAnswer(String answer, int answerNumber) {
        return String.format("\t%d) %s\n", answerNumber, answer);
    }

    private record TestQuestion(
            String question,
            int minAnswerNumber,
            int maxAnswerNumber,
            int correctAnswerNumber
    ) {
    }
}
