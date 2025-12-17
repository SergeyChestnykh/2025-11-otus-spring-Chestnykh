package ru.otus.hw.dao;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Service
public class ResourceProviderImpl implements ResourceProvider {
    @Override
    public Reader getResourceReader(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        InputStream is = resource.getInputStream();
        return new InputStreamReader(is, StandardCharsets.UTF_8);
    }
}
