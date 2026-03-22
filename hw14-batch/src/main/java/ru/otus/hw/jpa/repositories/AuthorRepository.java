package ru.otus.hw.jpa.repositories;

import ru.otus.hw.jpa.models.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository {
    List<Author> findAll();

    Optional<Author> findById(long id);
}
