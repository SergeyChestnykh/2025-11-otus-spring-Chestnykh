package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        List<Question> questions = questionDao.findAll();
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");

        char optionLetter;
        int qNumber = 1;

        for (Question question : questions) {
            ioService.printFormattedLine("%s. %s", qNumber++, question.text());
            optionLetter = 'a';
            for (Answer answer : question.answers()) {
                ioService.printFormattedLine("   %s) %s", optionLetter++, answer.text());
            }
            ioService.printLine("");
        }
    }
}
