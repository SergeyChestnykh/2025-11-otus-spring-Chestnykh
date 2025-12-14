package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.exceptions.QuestionReadException;

@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

    private final TestService testService;

    @Override
    public void run() {
        try {
            testService.executeTest();
        } catch (QuestionReadException e) {
            System.out.println("Невозможно прочитать список вопросов из файла!");
        }
    }
}
