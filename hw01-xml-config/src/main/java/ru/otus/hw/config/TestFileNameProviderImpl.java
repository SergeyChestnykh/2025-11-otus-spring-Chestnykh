package ru.otus.hw.config;

public class TestFileNameProviderImpl implements TestFileNameProvider {
    @Override
    public String getTestFileName() {
        return "questions.csv";
    }
}
