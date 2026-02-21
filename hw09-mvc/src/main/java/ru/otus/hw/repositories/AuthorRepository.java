package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import reactor.util.annotation.NonNull;
import ru.otus.hw.models.Author;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    @Override
    @NonNull
    List<Author> findAll();
}
