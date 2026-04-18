package ru.otus.hw.repositories;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.Author;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    @Override
    @NonNull
    List<Author> findAll();
}
