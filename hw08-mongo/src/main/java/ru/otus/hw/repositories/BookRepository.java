package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends MongoRepository<Book, Long> {
    @Override
    @NonNull
    List<Book> findAll();

    @Override
    @NonNull
    Optional<Book> findById(@NonNull Long id);
}
