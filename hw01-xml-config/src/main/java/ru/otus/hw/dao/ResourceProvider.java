package ru.otus.hw.dao;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;

public interface ResourceProvider {
    File getResourceFile(String fileName) throws URISyntaxException;

    Reader getResourceReader(String fileName) throws IOException;
}
