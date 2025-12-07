package ru.otus.hw.config;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TestFileNameProviderImpl implements TestFileNameProvider {

    private AppProperties appProperties;

    @Override
    public String getTestFileName() {
        return appProperties.getTestFileName();
    }
}
