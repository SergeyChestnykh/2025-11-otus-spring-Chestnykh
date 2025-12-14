package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() throws QuestionReadException {
        List<Question> questions = questionDao.findAll();
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        printQuestions(questions);
    }

    private void printQuestions(List<Question> questions) {
        int questionNumber = 1;
        for (Question question : questions) {
            ioService.printLine(questionToString(question, questionNumber++));
        }
    }

    private String questionToString(Question question, int questionNumber) {
        StringBuilder sb = new StringBuilder();

        sb.append(questionNumber)
                .append(". ")
                .append(question.text())
                .append(System.lineSeparator());

        int answerNumber = 1;
        for (Answer answer : question.answers()) {
            sb.append("   ")
                    .append(answerNumber++)
                    .append(") ")
                    .append(answer.text())
                    .append(System.lineSeparator());
        }

        return sb.toString();
    }
}
