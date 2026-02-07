package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import ru.otus.hw.models.Book;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Override
    @EntityGraph(attributePaths = {"author", "genres"})
    @NonNull
    List<Book> findAll();
}
