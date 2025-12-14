package ru.otus.hw.dao;

import java.io.IOException;
import java.io.Reader;

public interface ResourceProvider {
    Reader getResourceReader(String fileName) throws IOException;
}
