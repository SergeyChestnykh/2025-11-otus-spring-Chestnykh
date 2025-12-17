package ru.otus.hw.exceptions;

public class InvalidQuestionFormatException extends RuntimeException {
    public InvalidQuestionFormatException(String message) {
        super(message);
    }
}
