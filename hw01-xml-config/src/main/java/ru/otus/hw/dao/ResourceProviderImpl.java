package ru.otus.hw.dao;

import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ResourceProviderImpl implements ResourceProvider {
    @Override
    public File getResourceFile(String fileName) throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found!");
        } else {
            return new File(resource.toURI());
        }
    }

    @Override
    public Reader getResourceReader(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        InputStream is = resource.getInputStream();
        return new InputStreamReader(is, StandardCharsets.UTF_8);
    }
}
