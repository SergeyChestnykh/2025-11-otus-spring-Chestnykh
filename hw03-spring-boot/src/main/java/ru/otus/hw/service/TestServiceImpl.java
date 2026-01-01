package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService localizedIOService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        localizedIOService.printLine("");
        localizedIOService.printLineLocalized("TestService.answer.the.questions");
        var questions = questionDao.findAll();

        TestResult testResult = new TestResult(student);
        for (int i = 0; i < questions.size(); i++) {
            var question = questions.get(i);
            var isAnswerValid = askQuestion(question, i + 1);
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private boolean askQuestion(Question question, int questionNumber) {
        var answers = question.answers();
        var questionString = convertQuestionToString(question, questionNumber);
        localizedIOService.printLine(questionString);

        int userAnswerNumber = getUserAnswerNumber(question);
        return answers.get(userAnswerNumber - 1).isCorrect();
    }

    private int getUserAnswerNumber(Question question) {
        var answerPrompt = localizedIOService.getMessage(
                "TestService.write.number.from.to",
                1, question.answers().size()
        );
        String errorMessage = localizedIOService.getMessage("TestService.wrong.input");
        return localizedIOService.readIntForRangeWithPrompt(
                1,
                question.answers().size(),
                answerPrompt,
                errorMessage
        );
    }

    private String convertQuestionToString(Question question, int questionNumber) {
        var formattedQuestionText = String.format("%d. %s", questionNumber, question.text());
        var formattedQuestionsAsString = IntStream.range(0, question.answers().size())
                .mapToObj(i -> String.format("\t%d) %s", i + 1, question.answers().get(i).text()))
                .collect(Collectors.joining(System.lineSeparator()));

        return String.format("%s%n%s", formattedQuestionText, formattedQuestionsAsString);
    }
}
