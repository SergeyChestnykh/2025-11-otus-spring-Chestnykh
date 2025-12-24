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
import java.util.function.BiConsumer;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    /*
    * По Вашему указанию избавился от класса TestQuestion и вынес логику преобразования в отдельные функции.
    * Однако я все же считаю что данный класс был необходим и удобен, т.к. в текущей реализации логика назначения
    * номеров ответов для вывода, получения правильного варианта из данных и диапазона ответов разнесена по разным
    * функциям, что могло бы быть решено за один проход цикла. Если же сейчас решить данные вопросы в одной функции,
    * то будет нарушен принцип единственной ответственности. Плюс использование String.format(...) при наличии
    * ioService.printFormattedLine кажется излишним.*/

    private static final String ERROR_PROMPT = "Wrong input!!!";

    private static final String ANSWER_PROMPT_FORMAT = "Write number from %d to %d";

    private static final String QUESTION_FORMAT = "%d. %s";

    private static final String ANSWER_FORMAT = "\t%d) %s";

    public static final int INITIAL_ANSWER_NUMBER = 1;

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
            String questionString = convertQuestionToString(question, questionNumber++);

            ioService.printLine(questionString);

            int userAnswerNumber = getUserAnswerNumber(question);

            var isAnswerValid = userAnswerNumber == getCorrectAnswerNumber(question);
            applyAnswer.accept(question, isAnswerValid);
        }
    }

    private int getUserAnswerNumber(Question question) {
        var answerPrompt = String.format(
                ANSWER_PROMPT_FORMAT,
                INITIAL_ANSWER_NUMBER,
                getMaxAnswerNumber(question)
        );
        return ioService.readIntForRangeWithPrompt(
                INITIAL_ANSWER_NUMBER,
                getMaxAnswerNumber(question),
                answerPrompt,
                ERROR_PROMPT
        );
    }

    private String convertQuestionToString(Question question, int questionNumber) {
        var stringBuilder = new StringBuilder();

        String formattedQuestionText = String.format(QUESTION_FORMAT, questionNumber, question.text());
        stringBuilder.append(formattedQuestionText);
        stringBuilder.append("\n");

        AtomicInteger answerNumber = new AtomicInteger(INITIAL_ANSWER_NUMBER);
        question.answers().stream()
                .map(answer -> String.format(ANSWER_FORMAT, answerNumber.getAndIncrement(), answer.text()))
                .forEach(formattedAnswer -> {
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
